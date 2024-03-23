#include <am.h>
#include "../../riscv.h"

#define RTC_ADDR 0x02000000

void __am_timer_init() {
}

void __am_timer_uptime(AM_TIMER_UPTIME_T *uptime) {
  uint32_t high = inl(RTC_ADDR + 4);
  uint32_t low = inl(RTC_ADDR);
  uptime->us = (uint64_t)low + (((uint64_t)high) << 32);
}

void __am_timer_rtc(AM_TIMER_RTC_T *rtc) {
  rtc->second = 0;
  rtc->minute = 0;
  rtc->hour   = 0;
  rtc->day    = 0;
  rtc->month  = 0;
  rtc->year   = 1900;
}