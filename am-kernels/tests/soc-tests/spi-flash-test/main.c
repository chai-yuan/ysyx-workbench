#include "trap.h"

#define SPI_BASE 0x10001000L
#define SPI(offset) (*(volatile uint32_t*)((SPI_BASE) + (offset)))
#define SPI_CTRL 0x10
#define SPI_TX0 0x0
#define SPI_TX1 0x4
#define SPI_TX2 0x8
#define SPI_TX3 0xc
#define SPI_DIVIDER 0x14
#define SPI_SS 0x18

/* 初始化spi控制器 */
void spi_init() {
    SPI(SPI_DIVIDER) = 1;        // 尽可能高速的时钟
    uint32_t crtl = (1 << 13)    // ASS 自动SS信号
                    | (1 << 10)  // 在下降沿改变发送
                    // | (1 << 9)   // 在下降沿接收?
                    | 64;        // 字段长度64bit
    SPI(SPI_CTRL) = crtl;
}

// 大小端切换
uint32_t swap_endian(uint32_t value) {
    return (value >> 24) |               // 将最高位字节移到最低位
           ((value << 8) & 0x00FF0000) | // 将次高位字节移到次低位
           ((value >> 8) & 0x0000FF00) | // 将次低位字节移到次高位
           (value << 24);                // 将最低位字节移到最高位
}

uint32_t flash_read(uint32_t addr) {
    SPI(SPI_SS) = (1 << 0);    // 选择ss
    addr = addr & 0x00ffffff;  // 处理地址为24位地址
    SPI(SPI_TX1) = 0x03000000 | addr;
    SPI(SPI_CTRL) |= (1 << 8);  // 发送

    while ((SPI(SPI_CTRL) & (1 << 8)))
        ;  // 等待发送完毕

    return swap_endian(SPI(SPI_TX0));
}

int main() {
    spi_init();
    check(flash_read(0) == 0x03020101);
    check(flash_read(4) == 0x150d0805);
    // 通过测试
    halt(0);
}
