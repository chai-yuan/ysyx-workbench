#include <cpu/cpu.h>
#include <cpu/difftest.h>
#include <cpu/sim.h>
#include <device.h>
#include <memory/paddr.h>
#include <trace.h>

const paddr_t MROM_BASE = 0x20000000;
static uint8_t mrom_data[512];

void mrom_init() {
    Log("Use MROM");
}

uint8_t* mrom_raw_data() {
    return mrom_data;
}

extern "C" void mrom_read(uint32_t addr, uint32_t* data) {
    Log("Read at %x", addr);
    Log("Read %x",*(uint32_t*)(mrom_data + addr - MROM_BASE));
    *data = *(uint32_t*)(mrom_data + addr - MROM_BASE);
}
