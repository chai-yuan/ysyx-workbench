#ifndef device_H
#define device_H

#include <common.h>

size_t serial_write(const void* buf, size_t offset, size_t len);

size_t events_read(void* buf, size_t offset, size_t len);

size_t dispinfo_read(void* buf, size_t offset, size_t len);

size_t fb_write(const void* buf, size_t offset, size_t len);

void init_device();

#endif