/***************************************************************************************
 * Copyright (c) 2014-2022 Zihao Yu, Nanjing University
 *
 * NEMU is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan
 *PSL v2. You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 *
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY
 *KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 *NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *
 * See the Mulan PSL v2 for more details.
 ***************************************************************************************/

#include "local-include/reg.h"
#include <isa.h>

const char* regs[] = {"$0", "ra", "sp",  "gp",  "tp", "t0", "t1", "t2",
                      "s0", "s1", "a0",  "a1",  "a2", "a3", "a4", "a5",
                      "a6", "a7", "s2",  "s3",  "s4", "s5", "s6", "s7",
                      "s8", "s9", "s10", "s11", "t3", "t4", "t5", "t6"};

void isa_reg_display() {
    printf("pc: 0x%-10x\n", cpu.pc);
    // CSR
    printf("mepc: 0x%-10x\n", cpu.mepc);
    printf("mcause: 0x%-10x\n", cpu.mcause);
    printf("mstatus: 0x%-10x\n", cpu.mstatus);
    printf("mtvec: 0x%-10x\n", cpu.mtvec);

    for (int i = 0; i < 32; i += 2) {
        printf("x%-2d:%-6s: 0x%-10x %-12d | ", i, reg_name(i), gpr(i), gpr(i));
        if (i + 1 < 32) {
            printf("x%-2d:%-6s: 0x%-10x %-12d\n", i + 1, reg_name(i + 1),
                   gpr(i + 1), gpr(i + 1));
        }
    }
}

word_t isa_reg_str2val(const char* s, bool* success) {
    *success = false;
    for (int i = 0; i < 32; i++) {
        if (strcmp(regs[i], s) == 0) {
            *success = true;
            return gpr(i);
        }
    }
    if (strcmp(s, "pc") == 0) {
        *success = true;
        return cpu.pc;
    }

    return -1;
}

word_t* isa_csr_from_imm(word_t imm) {
    imm = imm & 0xfff;
    switch (imm) {
        case 0x341:
            return &(cpu.mepc);
        case 0x342:
            return &(cpu.mcause);
        case 0x300:
            return &(cpu.mstatus);
        case 0x305:
            return &(cpu.mtvec);
        case 0xf11:
            return &(cpu.mvenforid);
        case 0xf12:
            return &(cpu.marchid);
        default:
            panic("Unknown csr num : 0x%x", imm);
    }
}