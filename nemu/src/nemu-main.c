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

#include <common.h>
#include <readline/readline.h>
#include "./monitor/sdb/sdb.h"

void init_monitor(int, char*[]);
void am_init_monitor();
void engine_start();
int is_exit_status_bad();

void test() {
    int cnt = 0;
    unsigned ans;
    bool success;
    char *input, *input_exp, *input_ans;
    while ((input = readline(NULL))) {
        printf("input: %s", input);
        input_ans = strtok(input, " ");
        sscanf(input_ans, "%u", &ans);
        input_exp = input + strlen(input_ans) + 1;

        uint32_t eval_ans = expr(input_exp, &success);
        if (eval_ans != ans) {
            printf("In test: %d\n", cnt);
            printf("ans = %d , eval = %d\n", ans, eval_ans);
            exit(1);
        }
        cnt++;
    }
}

int main(int argc, char* argv[]) {
/* Initialize the monitor. */
#ifdef CONFIG_TARGET_AM
    am_init_monitor();
#else
    init_monitor(argc, argv);
#endif

    // test();

    /* Start engine. */
    engine_start();

    return is_exit_status_bad();
}
