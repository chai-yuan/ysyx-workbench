#ifndef SIM_H_
#define SIM_H_

#include <common.h>
#include "VysyxSoCFull.h"
#include "verilated.h"

void sim_init();
void sim_reset();
void sim_exit();
void sim_exec();

extern VysyxSoCFull* sim_soc;
extern word_t clk_cycle, valid_cycle;

void statistic();

#endif