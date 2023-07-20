/***************************************************************************************
 * Copyright (c) 2014-2022 Zihao Yu, Nanjing University
 *
 * NEMU is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 *
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *
 * See the Mulan PSL v2 for more details.
 ***************************************************************************************/

#include <isa.h>
#include <memory/vaddr.h>
/* We use the POSIX regex functions to process regular expressions.
 * Type 'man regex' for more information about POSIX regex functions.
 */
#include <regex.h>

enum {
    TK_NOTYPE = 256,
    TK_EQ,
    TK_NEQ,
    TK_AND,
    TK_DEREF,  // 解引用
    TK_REGNAME,
    TK_HEXNUM,
    TK_DECNUM
};

static struct rule {
    const char* regex;
    int token_type;
} rules[] = {
    {" +", TK_NOTYPE},  // spaces
    {"\\+", '+'},       // plus

    {"==", TK_EQ},  // equal
    {"!=", TK_NEQ},
    {"&&", TK_AND},

    {"\\-", '-'},  // minus
    {"\\*", '*'},  // multiply
    {"\\/", '/'},  // divide
    {"\\(", '('},  // left parenthesis
    {"\\)", ')'},  // right parenthesis

    {"\\$[a-z]{1,2}[0-9]*", TK_REGNAME},  // register name
    {"0[xX][0-9a-fA-F]+", TK_HEXNUM},     // hexadecimal number
    {"[0-9]+", TK_DECNUM}                 // decimal number
};

#define NR_REGEX ARRLEN(rules)

static regex_t re[NR_REGEX] = {};

/* Rules are used for many times.
 * Therefore we compile them only once before any usage.
 */
void init_regex() {
    int i;
    char error_msg[128];
    int ret;

    for (i = 0; i < NR_REGEX; i++) {
        ret = regcomp(&re[i], rules[i].regex, REG_EXTENDED);
        if (ret != 0) {
            regerror(ret, &re[i], error_msg, 128);
            panic("regex compilation failed: %s\n%s", error_msg, rules[i].regex);
        }
    }
}

#define TOKEN_BUF_SIZE 128

typedef struct token {
    int type;
    char str[32];
} Token;

static Token tokens[TOKEN_BUF_SIZE] __attribute__((used)) = {};
static int nr_token __attribute__((used)) = 0;

static bool make_token(char* e) {
    int position = 0;
    int i;
    regmatch_t pmatch;

    memset(tokens, 0, sizeof(tokens));
    nr_token = 0;

    while (e[position] != '\0') {
        /* Try all rules one by one. */
        for (i = 0; i < NR_REGEX; i++) {
            if (regexec(&re[i], e + position, 1, &pmatch, 0) == 0 && pmatch.rm_so == 0) {
                char* substr_start = e + position;
                int substr_len = pmatch.rm_eo;

                Log("match rules[%d] = \"%s\" at position %d with len %d: %.*s",
                    i, rules[i].regex, position, substr_len, substr_len, substr_start);

                position += substr_len;

                Assert(nr_token < TOKEN_BUF_SIZE, "Too many tokens");
                switch (rules[i].token_type) {
                    case TK_NOTYPE:
                        break;
                    case TK_EQ:
                    case TK_NEQ:
                    case TK_AND:
                    case TK_HEXNUM:
                    case TK_REGNAME:
                    case TK_DECNUM:
                    case '+':
                    case '-':
                    case '*':
                    case '/':
                    case '(':
                    case ')': {
                        tokens[nr_token].type = rules[i].token_type;
                        Assert(substr_len < 32, "Token length too long");
                        strncpy(tokens[nr_token].str, substr_start, substr_len);
                        nr_token++;
                        break;
                    }
                    default:
                        panic("Unknown token type");
                }

                break;
            }
        }

        if (i == NR_REGEX) {
            printf("no match at position %d\n%s\n%*.s^\n", position, e, position, "");
            return false;
        }
    }

    return true;
}

bool check_parentheses(uint32_t p, uint32_t q) {
    int left_cnt = 0;

    if (tokens[p].type != '(' || tokens[q].type != ')')
        return false;

    for (int i = p; i <= q; i++) {
        if (tokens[i].type == '(')
            left_cnt++;
        else if (tokens[i].type == ')')
            left_cnt--;

        if (left_cnt < 1 && i != q)
            return false;
    }
    return left_cnt == 0;
}

int get_priority(int token_type) {
    switch (token_type) {
        case TK_DEREF:
            return 4;
        case '*':
        case '/':
            return 3;
        case '+':
        case '-':
            return 2;
        case TK_EQ:
        case TK_NEQ:
            return 1;
        case TK_AND:
            return 0;
        default:
            return 32;
    }
}

uint32_t find_main_operator(uint32_t p, uint32_t q) {
    Assert(p <= q, "Bad expression");
    uint32_t left_cnt = 0;
    uint32_t pos = q - 1;
    for (uint32_t i = q; i > p; i--) {
        if (tokens[i].type == '(')
            left_cnt++;
        if (tokens[i].type == ')')
            left_cnt--;
        if (left_cnt == 0)
            if (tokens[i].type != '(' && tokens[i].type != ')' && tokens[i].type != TK_NOTYPE && tokens[i].type != TK_DECNUM) {
                if (get_priority(tokens[i].type) < get_priority(tokens[pos].type)) {
                    pos = i;
                }
            }
    }

    if (get_priority(tokens[p].type) < get_priority(tokens[pos].type))
        pos = p;
    return pos;
}

uint32_t eval(int p, int q) {
    if (p > q) {
        printf("%d,%d", p, q);
        panic("Bad expression");
    } else if (p == q) {
        uint32_t ret = 0;
        if (tokens[p].type == TK_DECNUM)
            sscanf(tokens[p].str, "%u", &ret);
        else if (tokens[p].type == TK_HEXNUM)
            sscanf(tokens[p].str, "%x", &ret);
        else if (tokens[p].type == TK_REGNAME) {
            bool success = true;
            ret = isa_reg_str2val(tokens[p].str + 1, &success);
            Assert(success, "Unable to obtain register value");
        }
        return ret;
    } else if (check_parentheses(p, q) == true) {
        // The expression is surrounded by a matched pair of parentheses.
        // If that is the case, just throw away the parentheses.
        return eval(p + 1, q - 1);
    } else {
        int op = find_main_operator(p, q);  // find the position of 主运算符 in the token expression
        uint32_t val1 = 0;
        if (tokens[op].type != TK_DEREF)
            val1 = eval(p, op - 1);
        uint32_t val2 = eval(op + 1, q);

        switch (tokens[op].type) {
            case '+':
                return val1 + val2;
            case '-':
                return val1 - val2;
            case '*':
                return val1 * val2;
            case '/':
                return val1 / val2;
            case TK_EQ:
                return val1 == val2;
            case TK_NEQ:
                return val1 != val2;
            case TK_AND:
                return val1 && val2;
            case TK_DEREF:
                return vaddr_read(val2, 4);
            default:
                panic("Operator does not exist");
        }
    }
    return 0;
}

word_t expr(char* e, bool* success) {
    if (!make_token(e)) {
        *success = false;
        return 0;
    }

    for (int i = 0; i < nr_token; i++) {
        if (tokens[i].type == '*' &&
            (i == 0 || (tokens[i - 1].type != TK_DECNUM && tokens[i - 1].type != TK_HEXNUM && tokens[i - 1].type != TK_REGNAME && tokens[i - 1].type != ')')))
            tokens[i].type = TK_DEREF;
    }

    *success = true;
    return eval(0, nr_token - 1);
}
