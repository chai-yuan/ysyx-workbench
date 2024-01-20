#include <cpu/cpu.h>
#include <cpu/difftest.h>
#include <cpu/sim.h>
#include <device.h>
#include <memory/paddr.h>
#include <trace.h>

static uint8_t flash_data[FLASH_END - FLASH_BASE];

void flash_init() {
    uint32_t init_data[] = {
        0x100007b7,
        0x04100713,
        0x00e78023,
        0x0000006f,
    };

    memcpy(flash_data,init_data,16);
}

uint8_t* flash_raw_data(){
    return flash_data;
}

extern "C" void flash_read(uint32_t addr, uint32_t* data) {
    addr = addr & 0x00FFFFFC;
    *data = *(uint32_t*)(flash_data + addr);
}