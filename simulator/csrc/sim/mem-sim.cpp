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

extern "C" void sim_mem_read(uint32_t addr, uint32_t *data) {
    addr = addr & 0x0FFFFFFC;
    *data = *(uint32_t *)(raw_mem + addr);
}

extern "C" void sim_mem_write(uint32_t addr, uint32_t size, uint32_t data) {
    addr = addr & 0x0FFFFFFF;
    // 根据要写入的尺寸，决定如何处理数据
    switch (size) {
    case 0: // 写入1字节
        *(uint8_t *)(raw_mem + addr) = (uint8_t)(data & 0xFF);
        break;
    case 1: // 写入2字节
        *(uint16_t *)(raw_mem + addr) = (uint16_t)(data & 0xFFFF);
        break;
    case 2: // 写入4字节
        *(uint32_t *)(raw_mem + addr) = data;
        break;
    }
}
