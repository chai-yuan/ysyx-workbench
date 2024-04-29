#include <cpu/cpu.h>
#include <cpu/difftest.h>
#include <memory/paddr.h>
#include <sim/sim.h>
#include <sim/trace.h>

#ifdef ysyxSoCFull
VysyxSoCFull *sim_top;
#elif CRRVTop
VCRRVTop *sim_top;
#endif

VerilatedContext *contextp;
VerilatedVcdC *tfp;

sim_statistic_t sim_statistic;
bool vtrace_enable;

void sim_init() {
    Log("sim init");

    contextp = new VerilatedContext();
    tfp = new VerilatedVcdC();

#ifdef ysyxSoCFull
    sim_top = new VysyxSoCFull();
#elif CRRVTop
    sim_top = new VCRRVTop();
#endif

    IFDEF(CONFIG_VTRACE, vtrace_init("debug.vcd"));

    sim_statistic.clock_cycle = sim_statistic.valid_cycle = 0;
    sim_reset();

    Log("sim init end, pc = 0x%x", cpu.pc);
}

void sim_reset() {
    sim_top->clock = 0;
    sim_top->reset = 1;
    sim_top->eval();
    IFDEF(CONFIG_VTRACE, dump_wave());

    for (int i = 0; i < 24; i++) {
        sim_top->clock = 1;
        sim_top->eval();
        IFDEF(CONFIG_VTRACE, dump_wave());
        sim_top->clock = 0;
        sim_top->eval();
        IFDEF(CONFIG_VTRACE, dump_wave());
    }

    sim_top->clock = 1;
    sim_top->reset = 0;
    sim_top->eval();
    IFDEF(CONFIG_VTRACE, dump_wave());
}

void sim_exit() {
    IFDEF(CONFIG_VTRACE, vtrace_exit());
    Log("sim exit");
}

void sim_exec() {
    sim_top->clock = 0;
    sim_top->eval();
    IFDEF(CONFIG_VTRACE, dump_wave());

    sim_top->clock = 1;
    sim_top->eval();
    IFDEF(CONFIG_VTRACE, dump_wave());

    sim_statistic.clock_cycle++;

#ifdef CONFIG_LOOP_CHECK
    if ((sim_statistic.clock_cycle % 0x200000) == 0)
        Log("check point : clock : %u, pc : 0x%x", sim_statistic.clock_cycle,
            cpu.pc);
#endif
}
