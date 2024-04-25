#include <sim/sim.h>
#include <sim/trace.h>

void vtrace_init(const char *vcd_file) {
    Log("vtrace on");
    contextp->traceEverOn(true);
    sim_top->trace(tfp, 0);
    tfp->open(vcd_file);
}

void vtrace_exit() {
    Log("vtrace exit");
    dump_wave();
    tfp->close();
}

void dump_wave() {
    if (vtrace_enable) {
        sim_top->eval();

        contextp->timeInc(1);
        tfp->dump(contextp->time());
    }
}
