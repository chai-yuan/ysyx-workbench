#include "trap.h"

int main() {
    unsigned int a = 0xcccccccd;
    unsigned int b = 0x8c;
    unsigned int result;

    __asm__ volatile (
        "mulhu %[res], %[op1], %[op2]\n\t"
        : [res] "=r" (result)
        : [op1] "r" (b), [op2] "r" (a)
        :
    );

    printf("%d\n",result);

    halt(0);
}
