#include <cstdlib>
#include "VCPUTop.h"
#include "verilated.h"

extern "C" {
void simulation_exit() {
    printf("Signal is high, exiting simulation.\n");
    exit(0);
}
}

unsigned int inst[32] = {
    0x00000000,
    0x00100073};

int main(int argc, char** argv, char** env) {
    Verilated::commandArgs(argc, argv);
    VCPUTop* top = new VCPUTop;

    while (true) {
        top->clock = ~top->clock;
        top->io_instSRAM_rdata = 0x00000000;
        printf("%d\n", top->io_instSRAM_addr);
        top->eval();

        top->clock = ~top->clock;
        top->io_instSRAM_rdata = 0x00000000;
        printf("%d\n", top->io_instSRAM_addr);
        top->eval();

        top->clock = ~top->clock;
        top->io_instSRAM_rdata = 0x00100073;
        printf("%d\n", top->io_instSRAM_addr);
        top->eval();
    }

    delete top;
    return 0;
}
