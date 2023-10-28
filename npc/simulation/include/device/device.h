#ifndef device_H
#define device_H

#include "common.h"
#define DEVICE_NUM 2

#define CONFIG_SERIAL_MMIO 0xa00003f8
#define CONFIG_RTC_MMIO 0xa0000048
#define CONFIG_FB_ADDR 0xa1000000

typedef void (*callback_t)(uint32_t, bool);

struct Device {
    word_t low, high;
    uint8_t* space;
    callback_t callback;
};

extern Device devices[DEVICE_NUM];

void init_device();

Device init_serial();
Device init_timer();

#endif