#ifndef TRACE_H_
#define TRACE_H_

#include <common.h>
#include "verilated.h"
#include "verilated_vcd_c.h"

#ifdef CONFIG_VTRACE

extern VerilatedContext* contextp;
extern VerilatedVcdC* tfp;

void vtrace_init(const char* vcd_file);

void vtrace_exit();

void dump_wave();

#endif

#endif