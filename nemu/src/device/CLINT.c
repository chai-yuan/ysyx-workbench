#include <device/map.h>
#include <utils.h>

/* https://chromitem-soc.readthedocs.io/en/latest/clint.html */

#define timerh 0xbffc
#define timerl 0xbff8

static uint8_t* clint_base = NULL;

static void clint_io_handler(uint32_t offset, int len, bool is_write) {
    if (!is_write) {  // 读数据，更新时间
        uint64_t us = get_time();
        clint_base[timerl] = (uint32_t)us;
        clint_base[timerh] = us >> 32;
    }
}

void init_clint() {
    clint_base = new_space(0x10000);
    add_mmio_map("clint", 0x2000000, clint_base, 0x10000, clint_io_handler);
}
