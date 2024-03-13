#include "trap.h"

#define GPIO_BASE 0x10002000
#define LED 0x0
#define SW 0x4
#define SEG 0x8

#define GPIO(x) *(volatile unsigned int *)(GPIO_BASE + x)

int main() {
    while(1){
        GPIO(LED) = GPIO(SW);
        for(volatile int i=0;i<100;i++);
    }

    halt(0);
}
