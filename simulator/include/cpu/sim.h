#ifndef SIM_H_
#define SIM_H_

#include <common.h>
#include "VysyxSoCFull.h"
#include "verilated.h"

void sim_init();
void sim_reset();
void sim_exit();
void sim_exec();

struct sim_statistic_t{
    word_t clock_cycle,valid_cycle;
};

extern VysyxSoCFull* sim_soc;
extern sim_statistic_t sim_statistic;
extern bool vtrace_enable;

void statistic();

#endif