#ifndef SIM_H_
#define SIM_H_

#include <common.h>
#include "VCPUTop.h"
#include "verilated.h"

void sim_init();
void sim_reset();
void sim_exit();
void sim_exec();

extern VCPUTop* sim_cpu;
extern word_t clk_cycle, valid_cycle;
extern word_t inst;

void statistic();

void update_cpu_state();

#endif