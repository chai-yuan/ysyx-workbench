#include <am.h>
#include <klib-macros.h>
#include "../riscv.h"

#define npc_trap(code) asm volatile("mv a0, %0; ebreak" : : "r"(code))

extern char _heap_start,_heap_end;
int main(const char* args);

Area heap = RANGE(&_heap_start, (&_heap_end));
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

void _trm_init() {
    int ret = main(mainargs);
    halt(ret);
}
