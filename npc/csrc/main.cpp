#include "Vtop.h"
#include "verilated.h"
#include "verilated_vcd_c.h"

int main(int argc, char** argv) {
    Verilated::commandArgs(argc, argv);
    Verilated::traceEverOn(true);

    Vtop* top = new Vtop;
    VerilatedVcdC* vcd = new VerilatedVcdC;
    top->trace(vcd, 32);
    if (argc > 1) {
        vcd->open(argv[1]);
    } else {
        vcd->open("trace.vcd");
    }

    vluint64_t time = 0;
    while (!Verilated::gotFinish() && time < 32) {
        top->a = rand() & 1;
        top->b = rand() & 1;

        top->eval();
        assert(top->f == top->a ^ top->b);

        vcd->dump(time);
        time++;
    }

    vcd->close();
    delete vcd;
    delete top;

    return 0;
}
