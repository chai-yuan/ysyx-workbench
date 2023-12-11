#include <device/device.h>
#include <memory/paddr.h>

bool check_serial_addr(int addr){
    return addr == CONFIG_SERIAL_MMIO;
}

void serial_read(int raddr, int* rdata) {
    panic("can not read serial!\n");
}

void serial_write(int waddr, int wdata, char wmask){
    printf("%c",(char)wdata);
}