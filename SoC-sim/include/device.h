#ifndef DEVICE_h
#define DEVICE_h

#include <common.h>

const paddr_t MROM_BASE = 0x20000000;
const paddr_t MROM_END = 0x20000fff;
uint8_t * mrom_raw_data();

const paddr_t FLASH_BASE = 0x30000000;
const paddr_t FLASH_END = 0x30ffffff;
void flash_init();

#endif