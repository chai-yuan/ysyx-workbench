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

#include <cpu/cpu.h>
#include "sdb.h"

#define NR_WP 32

static WP wp_pool[NR_WP] = {};
static WP *head = NULL, *free_ = NULL;

void init_wp_pool() {
    int i;
    for (i = 0; i < NR_WP; i++) {
        wp_pool[i].NO = i;
        wp_pool[i].next = (i == NR_WP - 1 ? NULL : &wp_pool[i + 1]);
    }

    head = NULL;
    free_ = wp_pool;
}

/* TODO: Implement the functionality of watchpoint */

WP* new_wp() {
    WP* tmp_free = free_;
    Assert(free_ != NULL, "There are no available watch points");
    free_ = free_->next;
    tmp_free->next = NULL;

    WP* head_idx = head;
    if (head_idx == NULL) {
        head = tmp_free;
    } else {
        while (head_idx->next)
            head_idx = head_idx->next;
        head_idx->next = tmp_free;
    }
    return tmp_free;
}

void free_wp(WP* wp) {
    WP* free_idx = free_;
    if (free_idx == NULL) {
        free_idx = wp;
        free_ = wp;
    } else {
        while (free_idx->next)
            free_idx = free_idx->next;
        free_idx->next = wp;
    }

    WP* head_idx = head;
    Assert(head_idx != NULL, "Free block not in head list");
    if (head_idx->NO == wp->NO) {
        head = head->next;
    } else {
        while (head_idx->next) {
            if (head_idx->next->NO == wp->NO) {
                head_idx->next = wp->next;
            }
            head_idx = head_idx->next;
        }
    }
    wp->next = NULL;
}

void check_wp(uint32_t pc, bool* success) {
    WP* head_idx = head;
    *success = false;
    bool suc = false;
    while (head_idx) {
        int new_val = expr(head_idx->exp, &suc);
        Assert(suc, "Watchpoint expression error");
        if (new_val != head_idx->val) {
            *success = true;
            printf("Watchpoint %d: %s\n", head_idx->NO, head_idx->exp);
            printf("Old value = %d\n", head_idx->val);
            printf("New value = %d\n", new_val);
            head_idx->val = new_val;
        }
        head_idx = head_idx->next;
    }
}

void delete_wp(int NO) {
    WP* head_idx = head;
    while (head_idx) {
        if (head_idx->NO == NO) {
            free_wp(head_idx);
            break;
        }
        head_idx = head_idx->next;
    }
}

void info_wp() {
    WP* head_idx = head;
    while (head_idx) {
        printf("Watch point: %d %s\nnow val: %d\n", head_idx->NO, head_idx->exp, head_idx->val);
        head_idx = head_idx->next;
    }
}