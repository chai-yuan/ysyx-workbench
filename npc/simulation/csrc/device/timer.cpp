#include <device/device.h>
#include <memory/paddr.h>
#include <sys/time.h>

static uint64_t boot_time = 0;

static uint64_t get_time_internal() {
    struct timeval now;
    gettimeofday(&now, NULL);
    uint64_t us = now.tv_sec * 1000000 + now.tv_usec;
    return us;
}

uint64_t get_time() {
    if (boot_time == 0)
        boot_time = get_time_internal();
    uint64_t now = get_time_internal();
    return now - boot_time;
}

static uint8_t timer_buf[8];

bool check_timer_addr(int addr) {
    return addr == CONFIG_RTC_MMIO || addr == CONFIG_RTC_MMIO + 4;
}

void timer_read(int raddr, int* rdata) {
    uint32_t* rtc_port_base = (uint32_t*)timer_buf;
    if (raddr == CONFIG_RTC_MMIO + 4) {
        uint64_t us = get_time();
        rtc_port_base[0] = (uint32_t)us;
        rtc_port_base[1] = us >> 32;
        *rdata = rtc_port_base[1];
    } else if (raddr == CONFIG_RTC_MMIO) {
        *rdata = rtc_port_base[0];
    }
}

void timer_write(int waddr, int wdata, char wmask) {
    waddr = waddr & ~0x3u;
    uint8_t* base_addr = &timer_buf[waddr-CONFIG_RTC_MMIO];

    for (int i = 0; i < 4; i++) {
        if (wmask & (1 << i)) {
            base_addr[i] = wdata & 0xFF;
        }
        wdata >>= 8;
    }
}
