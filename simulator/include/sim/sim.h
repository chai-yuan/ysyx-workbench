#ifndef SIM_H_
#define SIM_H_

#include "verilated.h"
#include <common.h>

#ifdef CONFIG_SOC
#include "VysyxSoCFull.h"
extern VysyxSoCFull *sim_top;
#elif CONFIG_NPC
#inlcude "VCRRVTop.h"
extern VCRRVTop *sim_top;
#endif

void sim_init();
void sim_reset();
void sim_exit();
void sim_exec();

struct sim_statistic_t {
    word_t clock_cycle, valid_cycle;
};

extern sim_statistic_t sim_statistic;
extern bool vtrace_enable;

void statistic();

#endif
