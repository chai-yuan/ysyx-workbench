#include <am.h>
#include "../riscv.h"

#define UART_BASE 0x10000000L
#define UART(offset) (*(volatile char*)((UART_BASE) + (offset)))
#define UART_TX 0   // 发送寄存器
#define UART_DLL 0  // 除数锁存寄存器低位
#define UART_DLM 1  // 除数锁存寄存器高位
#define UART_FCR 2  // FIFO控制寄存器
#define UART_LCR 3  // 线控寄存器
#define UART_LSR 5  // 线状态寄存器

void __am_uart_init(){
    UART(UART_LCR) = 0b10000011;  // 允许访问除数寄存器
    // 设置除数寄存器，写入顺序不能颠倒
    UART(UART_DLM) = 0x00;        // 高位
    UART(UART_DLL) = 0x01;        // 低位
    UART(UART_LCR) = 0b00000011;  // 关闭访问除数寄存器
    // 设置FIFO触发电平，这里保持默认就好
    // UART(UART_FCR) = ;
}

void __am_uart_tx(AM_UART_TX_T *tx){
    while (!(UART(UART_LSR) & (1 << 5)))
        ;
    *(volatile char*)(UART_BASE + UART_TX) = tx->data;
}

void __am_uart_rx(AM_UART_RX_T *rx){

}
