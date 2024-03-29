#ifndef CPU_H_
#define CPU_H_

#include <common.h>

typedef struct CPU_regs {
    word_t gpr[32];
    paddr_t pc;
    // CSR
    uint32_t mstatus;
    uint32_t mcause;
    uint32_t mepc;
    uint32_t mtvec;
    uint32_t mvenforid;
    uint32_t marchid;
    uint32_t mscratch;
    uint32_t mie;
    uint32_t mip;
    uint32_t mtval;
    // 其他状态
    uint32_t amo_addr;
    uint32_t privilege;
    bool sleep;
} CPU_regs;

extern CPU_regs cpu;

void cpu_exec(uint64_t n);

void assert_fail_msg();

void set_npc_state(int state, vaddr_t pc, int halt_ret);

#endif
