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

void timer_callback(uint32_t offset, bool is_write) {
    if (offset == 4 && !is_write) {
        uint32_t* rtc_port_base = (uint32_t*)timer_buf;
        uint64_t us = get_time();
        rtc_port_base[0] = (uint32_t)us;
        rtc_port_base[1] = us >> 32;
    }
}

Device init_timer() {
    Device new_device;
    new_device.low = CONFIG_RTC_MMIO;
    new_device.high = new_device.low + sizeof(timer_buf);
    new_device.space = timer_buf;
    new_device.callback = timer_callback;
    return new_device;
}
