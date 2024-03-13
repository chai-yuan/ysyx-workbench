#include <am.h>
#include <klib-macros.h>
#include <klib.h>
#include "../riscv.h"

#define npc_trap(code) asm volatile("mv a0, %0; ebreak" : : "r"(code))

extern char _heap_start, _heap_end;

int main(const char* args);

Area heap = RANGE(&_heap_start, &_heap_end);
#ifndef MAINARGS
#define MAINARGS ""
#endif
static const char mainargs[] = MAINARGS;

void __am_uart_init();
void putch(char ch) {
    AM_UART_TX_T tx;
    tx.data = ch;
    ioe_write(AM_UART_TX,&tx);
}

void halt(int code) {
    npc_trap(code);
    while (1)
        ;
}

void display_npc_info(){
    unsigned int mvendorid, marchid;
    char mvendorid_s[5] = {0};
    asm volatile ("csrr %0, mvendorid" : "=r"(mvendorid) :: "memory");
    asm volatile ("csrr %0, marchid" : "=r"(marchid) :: "memory");
    memcpy(mvendorid_s,&mvendorid,4);
    printf("mvendorid : %s\nmarchid : %d\n",mvendorid_s,marchid);
    // 通过数码管显示
    unsigned int seg_marchid = 0;
    while (marchid){
        seg_marchid = seg_marchid << 4;
        seg_marchid += marchid%10;
        marchid /= 10;
    }
    *(volatile unsigned int *)(0x10002008) = seg_marchid;
}

void _trm_init() {
    __am_uart_init();
    display_npc_info();
    int ret = main(mainargs);
    halt(ret);
}
