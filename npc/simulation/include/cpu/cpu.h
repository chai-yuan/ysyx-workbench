#ifndef CPU_H_
#define CPU_H_

#include <common.h>

typedef struct cpu_regs {
    paddr_t pc, last_pc;
    word_t gpr[32];
} cpu_regs;

extern cpu_regs cpu;

void cpu_exec(uint64_t n);

void assert_fail_msg();

void set_npc_state(int state, vaddr_t pc, int halt_ret);

#endif