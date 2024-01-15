#include <cpu/cpu.h>
#include <cpu/difftest.h>
#include <cpu/sim.h>
#include <device.h>
#include <memory/paddr.h>
#include <trace.h>

static uint8_t mrom_data[MROM_END - MROM_BASE];

uint8_t* mrom_raw_data() {
    return mrom_data;
}

extern "C" void mrom_read(uint32_t addr, uint32_t* data) {
    addr = addr & 0x0000FFFC;
    *data = *(uint32_t*)(mrom_data + addr);
}
