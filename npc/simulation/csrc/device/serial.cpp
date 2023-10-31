#include <device/device.h>
#include <memory/paddr.h>

static uint8_t serial_buf[8];

static void serial_putc(char ch) {
    putc(ch, stderr);
}

void serial_callback(uint32_t offset, bool is_write) {
    if (is_write) {
        serial_putc(serial_buf[0]);
    }
}

Device init_serial() {
    Device new_device;
    new_device.low = CONFIG_SERIAL_MMIO;
    new_device.high = new_device.low + sizeof(serial_buf);
    new_device.space = serial_buf;
    new_device.callback = serial_callback;
    return new_device;
}
