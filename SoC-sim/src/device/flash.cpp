#include <cpu/cpu.h>
#include <cpu/difftest.h>
#include <cpu/sim.h>
#include <device.h>
#include <memory/paddr.h>
#include <trace.h>

static uint8_t flash_data[FLASH_END - FLASH_BASE];

void flash_init() {
    // 随便放个斐波那契数列吧
    flash_data[0] = flash_data[1] = 1;
    for (int i = 2; i < 16; i++) {
        flash_data[i] = flash_data[i - 2] + flash_data[i - 1];
    }
}

extern "C" void flash_read(uint32_t addr, uint32_t* data) {
    addr = addr & 0x00FFFFFC;
    *data = *(uint32_t*)(flash_data + addr);
}