#include <device/map.h>
#include <utils.h>

/* https://chromitem-soc.readthedocs.io/en/latest/clint.html */

#define timerh 0xbffc
#define timerl 0xbff8
#define timermatchh 0x4004
#define timermatchl 0x4000

static uint8_t* clint_base = NULL;

bool clint_check_intr() {
    return ((clint_base[timerh] > clint_base[timermatchh]) ||
            (clint_base[timerh] == clint_base[timermatchh] &&
             clint_base[timerl] > clint_base[timermatchl])) &&
           (clint_base[timermatchh] || clint_base[timermatchl]);
}

static void clint_io_handler(uint32_t offset, int len, bool is_write) {
    if (!is_write) {  // 读数据，更新时间
        uint64_t us = get_time();
        clint_base[timerl] = (uint32_t)us;
        clint_base[timerh] = us >> 32;
    } else if (offset == timermatchl || offset == timermatchh) {
        Log("timermatch : %lld", (long long)clint_base[timermatchl]);
    }
}

void init_clint() {
    clint_base = new_space(0x10000);
    clint_base[timermatchl] = clint_base[timermatchh] = 0;
    add_mmio_map("clint", 0x2000000, clint_base, 0x10000, clint_io_handler);
}
