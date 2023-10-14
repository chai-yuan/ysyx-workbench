#include <memory/mem-sim.h>
#include <memory/paddr.h>

extern "C" void verilog_pmem_read(int raddr, int* rdata) {
    pmem_read(raddr, (word_t*)rdata);
//    inst = *rdata;
#ifdef CONFIG_MTRACE
    Log("pmem_read(addr:0x%08x,rdata:0x%08x)", raddr, *rdata);
#endif
}

extern "C" void verilog_pmem_write(int waddr, int wdata, char wmask) {
    pmem_write(waddr, wdata, wmask);
#ifdef CONFIG_MTRACE
    Log("pmem_write(addr:0x%08x,wdata:0x%08x,mask:%x)", waddr, wdata, wmask);
#endif
}