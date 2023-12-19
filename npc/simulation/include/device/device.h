#ifndef device_H
#define device_H

#include "common.h"

#define CONFIG_SERIAL_MMIO 0xa00003f8
#define CONFIG_RTC_MMIO 0xa0000048
#define CONFIG_FB_ADDR 0xa1000000

void timer_read(int raddr, int* rdata);
void timer_write(int waddr, int wdata, char wmask);

void serial_read(int raddr, int* rdata);
void serial_write(int waddr, int wdata, char wmask);

#endif