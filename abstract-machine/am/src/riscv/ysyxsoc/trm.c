#include <am.h>
#include <klib-macros.h>
#include "../riscv.h"

#define npc_trap(code) asm volatile("mv a0, %0; ebreak" : : "r"(code))

extern char _heap_start, _heap_end;
extern char _etext, _bdata, _edata;

int main(const char* args);

Area heap = RANGE(&_heap_start, &_heap_end);
#ifndef MAINARGS
#define MAINARGS ""
#endif
static const char mainargs[] = MAINARGS;

#define UART_BASE 0x10000000L
#define UART_TX   0
void putch(char ch) {
    *(volatile char *)(UART_BASE + UART_TX) = ch;
}

void halt(int code) {
    npc_trap(code);
    while (1)
        ;
}

/* 将储存这mrom当中的一些数据拷贝到sram当中执行 */
inline void bootloader() {
    // 确定拷贝的目标地址
    char *bdata_p = &_bdata, *edata_p = &_edata;
    for(char *text_p = &_etext; bdata_p != edata_p; text_p++,bdata_p++){
        *bdata_p = *text_p; // 从mrom拷贝到sram
    }
}

void _trm_init() {
    bootloader();
    int ret = main(mainargs);
    halt(ret);
}
