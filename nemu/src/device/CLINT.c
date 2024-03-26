#include <device/map.h>
#include <utils.h>

/* https://chromitem-soc.readthedocs.io/en/latest/clint.html */

#define timerh 0xbffc
#define timerl 0xbff8
#define timermatchh 0x4004
#define timermatchl 0x4000

static uint8_t* clint_base = NULL;

#define CLINT(addr) *((uint32_t*)(clint_base + addr))

void clint_time_update() {
    uint64_t us = get_time();
    CLINT(timerl) = (uint32_t)us;
    CLINT(timerh) = us >> 32;
}

bool clint_check_intr() {
    clint_time_update();
    if (CLINT(timerh) > CLINT(timermatchh)) {
        return (CLINT(timermatchl) || CLINT(timermatchh));
    } else if (CLINT(timerh) == CLINT(timermatchh) &&
               CLINT(timerl) > CLINT(timermatchl)) {
        return (CLINT(timermatchl) || CLINT(timermatchh));
    }
    return false;
}

static void clint_io_handler(uint32_t offset, int len, bool is_write) {
    if (!is_write) {  // 读数据，更新时间
        clint_time_update();
    } else if (offset == timermatchl || offset == timermatchh) {
    }
}

void init_clint() {
    clint_base = new_space(0x10000);
    CLINT(timermatchl) = 0;
    CLINT(timermatchh) = 0;
    add_mmio_map("clint", 0x2000000, clint_base, 0x10000, clint_io_handler);
}
