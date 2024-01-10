#include <cpu/cpu.h>
#include <cpu/difftest.h>
#include <cpu/sim.h>
#include <device.h>
#include <memory/paddr.h>
#include <trace.h>

static uint8_t mrom_data[MROM_END - MROM_BASE];

void mrom_init() {
    Log("Use MROM");
}

uint8_t* mrom_raw_data() {
    return mrom_data;
}

extern "C" void mrom_read(uint32_t addr, uint32_t* data) {
    *data = *(uint32_t*)(mrom_data + addr - MROM_BASE);
}
