#include <memory/mem-sim.h>
#include <memory/paddr.h>

inline void sram_mem_sim() {
    paddr_t instAddr = cpu_top->io_instSRAM_addr;
    pmem_read(instAddr, &inst);
    cpu_top->io_instSRAM_rdata = inst;

    cpu_top->eval();

    paddr_t dataAddr = cpu_top->io_dataSRAM_addr;
    char dataWe = cpu_top->io_dataSRAM_we;

    if (dataWe) {
        if (cpu_top->io_dataSRAM_en) {
            pmem_write(dataAddr, cpu_top->io_dataSRAM_wdata, dataWe);
        } else {
            word_t read_data;
            pmem_read(dataAddr, &read_data);
            cpu_top->io_dataSRAM_rdata = read_data;
        }
    }

    cpu_top->eval();
}
