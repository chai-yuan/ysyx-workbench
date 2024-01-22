#include <am.h>
#include <klib-macros.h>
#include <klib.h>
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
#define UART(offset) (*(volatile char*)((UART_BASE) + (offset)))
#define UART_TX 0   // 发送寄存器
#define UART_DLL 0  // 除数锁存寄存器低位
#define UART_DLM 1  // 除数锁存寄存器高位
#define UART_FCR 2  // FIFO控制寄存器
#define UART_LCR 3  // 线控寄存器
#define UART_LSR 5  // 线状态寄存器

/* 初始化串口 */
void uart_init() {
    UART(UART_LCR) = 0b10000011;  // 允许访问除数寄存器
    // 设置除数寄存器，写入顺序不能颠倒
    UART(UART_DLM) = 0x00;        // 高位
    UART(UART_DLL) = 0x01;        // 低位
    UART(UART_LCR) = 0b00000011;  // 关闭访问除数寄存器
    // 设置FIFO触发电平，这里保持默认就好
    // UART(UART_FCR) = ;
}

void putch(char ch) {
    while (!(UART(UART_LSR) & (1 << 5)))
        ;
    *(volatile char*)(UART_BASE + UART_TX) = ch;
}

void halt(int code) {
    npc_trap(code);
    while (1)
        ;
}

/* 将储存这flash当中的一些数据拷贝到sram当中执行 */
inline void bootloader() {
    // 确定拷贝的目标地址
    char *bdata_p = &_bdata, *edata_p = &_edata;
    for (char* text_p = &_etext; bdata_p != edata_p; text_p++, bdata_p++) {
        *bdata_p = *text_p;  // 从mrom拷贝到sram
    }
}

void display_npc_info(){
    unsigned int mvendorid, marchid;
    char mvendorid_s[5] = {0};
    asm volatile ("csrr %0, mvendorid" : "=r"(mvendorid) :: "memory");
    asm volatile ("csrr %0, marchid" : "=r"(marchid) :: "memory");
    memcpy(mvendorid_s,&mvendorid,4);
    printf("mvendorid : %s\nmarchid : %d\n",mvendorid_s,marchid);
}

void _trm_init() {
    bootloader();
    uart_init();
    display_npc_info();
    int ret = main(mainargs);
    halt(ret);
}
