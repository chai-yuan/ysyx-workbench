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

/* We use the POSIX regex functions to process regular expressions.
 * Type 'man regex' for more information about POSIX regex functions.
 */
#include <regex.h>

enum {
    TK_NOTYPE = 256,
    TK_EQ,
    TK_DECIMAL
};

static struct rule {
    const char* regex;
    int token_type;
} rules[] = {
    {" +", TK_NOTYPE},       // spaces
    {"\\+", '+'},            // plus
    {"==", TK_EQ},           // equal
    {"\\-", '-'},            // minus
    {"\\*", '*'},            // multiply
    {"\\/", '/'},            // divide
    {"\\(", '('},            // left parenthesis
    {"\\)", ')'},            // right parenthesis
    {"[0-9]+", TK_DECIMAL},  // decimal number
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

                /* TODO: Now a new token is recognized with rules[i]. Add codes
                 * to record the token in the array `tokens'. For certain types
                 * of tokens, some extra actions should be performed.
                 */
                Assert(nr_token < TOKEN_BUF_SIZE, "Too many tokens");
                switch (rules[i].token_type) {
                    case TK_NOTYPE:
                        break;
                    case TK_EQ:
                        tokens[nr_token].type = TK_EQ;
                        nr_token++;
                        break;
                    case TK_DECIMAL:
                        tokens[nr_token].type = TK_DECIMAL;
                        Assert(substr_len < 32, "Number length too long");
                        strncpy(tokens[nr_token].str, substr_start, substr_len);
                        nr_token++;
                        break;
                    case '+':
                        tokens[nr_token].type = '+';
                        nr_token++;
                        break;
                    case '-':
                        tokens[nr_token].type = '-';
                        nr_token++;
                        break;
                    case '*':
                        tokens[nr_token].type = '*';
                        nr_token++;
                        break;
                    case '/':
                        tokens[nr_token].type = '/';
                        nr_token++;
                        break;
                    case '(':
                        tokens[nr_token].type = '(';
                        nr_token++;
                        break;
                    case ')':
                        tokens[nr_token].type = ')';
                        nr_token++;
                        break;
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

int get_priority(char token_type) {
    switch (token_type) {
        case '*':
        case '/':
            return 2;
        case '+':
        case '-':
            return 1;
        default:
            return 32;
    }
}

// int find_main_operator(int p, int q) {
//     int ret = p, left_cnt = 0;
//     char now_op = '?';
//     for (int i = p; i <= q; i++) {
//         if (tokens[i].type == '(') {
//             left_cnt++;
//             while (true) {
//                 i++;
//                 if (tokens[i].type == '(')
//                     left_cnt++;
//                 else if (tokens[i].type == ')')
//                     left_cnt--;
//                 if (left_cnt == 0)
//                     break;
//             }
//         } else if (tokens[i].type == TK_DECIMAL) {
//             continue;
//         } else if (get_priority(tokens[i].type) >= get_priority(now_op)) {
//             now_op = tokens[i].type;
//             ret = i;
//         }
//     }
//     return ret;
// }

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
            if (tokens[i].type != '(' && tokens[i].type != ')' && tokens[i].type != TK_NOTYPE && tokens[i].type != TK_DECIMAL) {
                if (get_priority(tokens[i].type) < get_priority(tokens[pos].type)) {
                    pos = i;
                }
            }
    }

    return pos;
}

uint32_t eval(int p, int q) {
    if (p > q) {
        printf("%d,%d", p, q);
        panic("Bad expression");
    } else if (p == q) {
        uint32_t ret = 0;
        sscanf(tokens[p].str, "%d", &ret);
        return ret;
    } else if (check_parentheses(p, q) == true) {
        // The expression is surrounded by a matched pair of parentheses.
        // If that is the case, just throw away the parentheses.
        return eval(p + 1, q - 1);
    } else {
        int op = find_main_operator(p, q);  // find the position of 主运算符 in the token expression
        uint32_t val1 = eval(p, op - 1);
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

    *success = true;
    return eval(0, nr_token - 1);
}
