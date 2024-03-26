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

#include <isa.h>

word_t isa_raise_intr(word_t NO, vaddr_t epc) {
#ifdef CONFIG_ETRACE
    Log("Excepion : %d", NO);
#endif
    if (NO & 0x80000000) {  // 中断
        cpu.mtval = 0;
    } else {
        // cpu.mtval = epc;  // 异常
    }
    cpu.mcause = NO;
    cpu.mepc = epc;
    cpu.mstatus = ((cpu.mstatus & 0x08) << 4) |
                  ((cpu.privilege & 0x03) << 11);  // 设置MPIE和MPP
    cpu.privilege = 3;
    return cpu.mtvec;
}

bool clint_check_intr();

word_t isa_query_intr() {
    if (clint_check_intr()){
        return INTR_CLINT;
    }
    return INTR_EMPTY;
}
