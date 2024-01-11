#include <cpu/cpu.h>
#include <cpu/difftest.h>
#include <cpu/sim.h>
#include <memory/paddr.h>
#include <trace.h>

VysyxSoCFull* sim_soc;
VerilatedContext* contextp;
VerilatedVcdC* tfp;

sim_statistic_t sim_statistic;

void sim_init() {
    Log("sim init");

    contextp = new VerilatedContext();
    tfp = new VerilatedVcdC();
    sim_soc = new VysyxSoCFull();
    IFDEF(CONFIG_VTRACE, vtrace_init("debug.vcd"));

    sim_statistic.clock_cycle = sim_statistic.valid_cycle = 0;
    sim_reset();

    Log("sim init end, pc = 0x%x", cpu.pc);
}

void sim_reset() {
    sim_soc->clock = 0;
    sim_soc->reset = 1;
    sim_soc->eval();
    IFDEF(CONFIG_VTRACE, dump_wave());

    for (int i = 0; i < 24; i++) {
        sim_soc->clock = 1;
        sim_soc->eval();
        IFDEF(CONFIG_VTRACE, dump_wave());
        sim_soc->clock = 0;
        sim_soc->eval();
        IFDEF(CONFIG_VTRACE, dump_wave());
    }

    sim_soc->clock = 1;
    sim_soc->reset = 0;
    sim_soc->eval();
    IFDEF(CONFIG_VTRACE, dump_wave());
}

void sim_exit() {
    IFDEF(CONFIG_VTRACE, vtrace_exit());
    Log("sim exit");
}

void sim_exec() {
    sim_soc->clock = 0;
    sim_soc->eval();
    IFDEF(CONFIG_VTRACE, dump_wave());

    sim_soc->clock = 1;
    sim_soc->eval();
    IFDEF(CONFIG_VTRACE, dump_wave());

    sim_statistic.clock_cycle++;
}
