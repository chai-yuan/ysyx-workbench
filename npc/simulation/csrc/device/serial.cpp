#include <device/device.h>
#include <memory/paddr.h>
#include <unistd.h>

bool check_serial_addr(int addr) {
    return addr == CONFIG_SERIAL_MMIO;
}

void serial_read(int raddr, int* rdata) {
}

void serial_write(int waddr, int wdata, char wmask) {
    printf("%c", (char)wdata);
    fflush(stdout);
}