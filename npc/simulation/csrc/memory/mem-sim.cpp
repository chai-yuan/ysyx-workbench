#include <memory/mem-sim.h>
#include <memory/paddr.h>

void sram_mem_sim() {
    paddr_t instAddr = cpu_top->io_inst_addr;
    pmem_read(instAddr, &inst);
    cpu_top->io_inst_readData = inst;

    paddr_t dataAddr = cpu_top->io_data_addr;
    char dataMark = cpu_top->io_data_mark;

    if (cpu_top->io_data_readEn) {
        word_t readData;
        pmem_read(dataAddr, &readData);
        cpu_top->io_data_readData = readData;
    }
    if (cpu_top->io_data_writeEn) {
        pmem_write(dataAddr, cpu_top->io_data_writeData, dataMark);
    }

    cpu_top->eval();
}
