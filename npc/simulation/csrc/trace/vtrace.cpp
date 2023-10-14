#include <cpu/sim.h>
#include <trace.h>

void vtrace_init(const char* vcd_file) {
    Log("vtrace on");
    contextp->traceEverOn(true);
    sim_cpu->trace(tfp, 0);
    tfp->open(vcd_file);
}

void vtrace_exit() {
    Log("vtrace exit");
    dump_wave();
    tfp->close();
}

void dump_wave() {
    sim_cpu->eval();

    contextp->timeInc(1);
    tfp->dump(contextp->time());
}
