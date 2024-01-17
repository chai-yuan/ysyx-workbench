#include "trap.h"

#define SPI_BASE 0x10001000L
#define SPI(offset) (*(volatile uint32_t*)((SPI_BASE) + (offset)))
#define SPI_CTRL 0x10
#define SPI_TX 0x0
#define SPI_RX 0x0
#define SPI_DIVIDER 0x14
#define SPI_SS 0x18

/* 初始化spi控制器 */
void spi_init() {
    SPI(SPI_DIVIDER) = 1;      // 尽可能高速的始终
    uint32_t crtl = (1 << 13)  // ASS 自动SS信号
                    | 16;      // 字段长度16bit
    SPI(SPI_CTRL) = crtl;
}

uint8_t spi_sent_u8(uint8_t data, uint8_t ss) {
    SPI(SPI_SS) = (1 << ss);
    SPI(SPI_TX) = (data << 8);
    SPI(SPI_CTRL) = SPI(SPI_CTRL) | (1 << 8);  // 发送

    while ((SPI(SPI_CTRL) & (1 << 8)))
        ;  // 等待发送完毕

    return SPI(SPI_RX);
}

int main() {
    spi_init();
    uint8_t data = spi_sent_u8(0b01110001, 7);
    check(data == 0b10001110);
    // 通过测试
    halt(0);
}
