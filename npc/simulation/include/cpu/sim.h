#ifndef SIM_H_
#define SIM_H_

#include <common.h>
#include "VCPUTop.h"
#include "verilated.h"

void sim_init();
void sim_reset();
void sim_exit();
void sim_exec();
void sim_mem();

extern VCPUTop* cpu_top;
extern word_t cycle_num;
extern word_t inst;

void update_regs();

#endif