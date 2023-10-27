#include <device/device.h>

Device devices[DEVICE_NUM];

void init_device() {
    Log("Device: %s", ANSI_FMT("ON", ANSI_FG_GREEN));
    devices[0] = init_serial();
}