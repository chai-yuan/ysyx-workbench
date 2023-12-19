#include <device/device.h>
#include <memory/paddr.h>
#include <unistd.h>

void serial_read(int raddr, int* rdata) {
}

void serial_write(int waddr, int wdata, char wmask) {
    printf("%c", (char)wdata);
    fflush(stdout);
}