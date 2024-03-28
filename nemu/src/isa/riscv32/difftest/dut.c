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

#include <cpu/difftest.h>
#include <isa.h>
#include "../local-include/reg.h"

#define CHECK_REG(name)                                                      \
    if (ref_r->name != cpu.name) {                                           \
        printf("difftest fail at " #name ",ref value: 0x%x\n", ref_r->name); \
        return false;                                                        \
    }

bool isa_difftest_checkregs(CPU_state* ref_r, vaddr_t pc) {
    int reg_num = ARRLEN(cpu.gpr);

    CHECK_REG(pc);
    CHECK_REG(mstatus);
    CHECK_REG(mcause);
    CHECK_REG(mepc);
    CHECK_REG(mtvec);
    CHECK_REG(mscratch);
    CHECK_REG(mie);
    CHECK_REG(mip);
    CHECK_REG(mtval);
    for (int i = 0; i < reg_num; i++) {
        if (ref_r->gpr[i] != cpu.gpr[i]) {
            printf("difftest fail at %s ,ref value: 0x%x\n", reg_name(i),
                   ref_r->gpr[i]);
            return false;
        }
    }
    return true;
}

void isa_difftest_attach() {}
