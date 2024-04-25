#include <cpu/cpu.h>
#include <cpu/difftest.h>
#include <cstdint>
#include <memory/paddr.h>
#include <sim/sim.h>
#include <sim/trace.h>

extern uint8_t *raw_mem;

extern "C" void flash_read(uint32_t addr, uint32_t *data) {
    addr = addr & 0x0FFFFFFC;
    if ((addr % 4096) == 0)
        Log("FLASH LOAD at 0x%x %d", addr, addr);
    *data = *(uint32_t *)(raw_mem + addr);
}

extern "C" void mrom_read(uint32_t addr, uint32_t *data) {
    addr = addr & 0x0FFFFFFC;
    if ((addr % 4096) == 0)
        Log("MROM LOAD at 0x%x %d", addr, addr);
    *data = *(uint32_t *)(raw_mem + addr);
}

extern "C" void mem_read(uint32_t addr, uint32_t *data) {
    addr = addr & 0x0FFFFFFC;
    *data = *(uint32_t *)(raw_mem + addr);
}
