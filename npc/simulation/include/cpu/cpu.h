#ifndef CPU_H_
#define CPU_H_

#include <common.h>

void cpu_exec(uint64_t n);

void assert_fail_msg();

void set_nemu_state(int state, vaddr_t pc, int halt_ret);

#endif