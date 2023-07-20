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

#include <assert.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

// this should be enough
#define BUF_SIZE 4096
static char buf[BUF_SIZE] = {};
static char print_buf[BUF_SIZE] = {};
static int buf_idx, print_buf_idx;
static char code_buf[BUF_SIZE + 128] = {};  // a little larger than `buf`
static char* code_format =
    "#include <stdio.h>\n"
    "int main() { "
    "  unsigned result = %s; "
    "  printf(\"%%u\", result); "
    "  return 0; "
    "}";

int choose(int n) {
    if (buf_idx > 64)
        return 0;
    return rand() % n;
}

void gen(char c) {
    if (buf_idx >= BUF_SIZE - 1) {
        printf("Buffer overflow!\n");
        exit(1);
    }
    if (c != 'U')
        print_buf[print_buf_idx++] = c;
    buf[buf_idx++] = c;
}

void gen_num() {
    int num = rand() % 100;  // Generate a random number less than 1000
    int len = snprintf(buf + buf_idx, BUF_SIZE - buf_idx, "%d", num);
    snprintf(print_buf + print_buf_idx, BUF_SIZE - print_buf_idx, "%d", num);
    if (len >= BUF_SIZE - buf_idx) {
        printf("Buffer overflow!\n");
        exit(1);
    }
    buf_idx += len;
    print_buf_idx += len;
    gen('U');
}

void gen_rand_op() {
    char ops[] = "+-*/";
    gen(ops[choose(4)]);  // Choose a random operator from ops
}

void gen_rand_space() {
    for (int i = 0; i < choose(4); i++) {
        gen(' ');
    }
}

void gen_rand_expr() {
    // printf("%s\n", buf);
    switch (choose(3)) {
        case 0:
            gen_num();
            gen_rand_space();
            break;
        case 1:
            gen('(');
            gen_rand_space();
            gen_rand_expr();
            gen(')');
            break;
        default:
            gen_rand_expr();
            gen_rand_space();
            gen_rand_op();
            gen_rand_space();
            gen_rand_expr();
            break;
    }
}

int main(int argc, char* argv[]) {
    int seed = time(0);
    srand(seed);
    int loop = 1;
    if (argc > 1) {
        sscanf(argv[1], "%d", &loop);
    }

    int i;
    for (i = 0; i < loop; i++) {
        buf_idx = 0;
        print_buf_idx = 0;
        memset(print_buf, 0, sizeof(print_buf));
        memset(buf, 0, sizeof(buf));
        gen_rand_expr();

        sprintf(code_buf, code_format, buf);

        FILE* fp = fopen("/tmp/.code.c", "w");
        assert(fp != NULL);
        fputs(code_buf, fp);
        fclose(fp);

        int ret = system("gcc -Werror /tmp/.code.c -o /tmp/.expr");
        if (ret != 0) {
            continue;
        }

        fp = popen("/tmp/.expr", "r");
        assert(fp != NULL);

        int result;
        ret = fscanf(fp, "%d", &result);
        pclose(fp);

        printf("%u %s\n", result, print_buf);
    }
    return 0;
}
