#include <device/device.h>
#include <memory/paddr.h>
#include <unistd.h>

extern "C" void verilog_serial_read(int raddr, int* rdata) {
    serial_read(raddr, rdata);
}

extern "C" void verilog_serial_write(int waddr, int wdata, char wmask) {
    serial_write(waddr, wdata, wmask);
}