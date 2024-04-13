#include <stdio.h>
#include "trap.h"
unsigned char mem[32];

int main() {
    unsigned int a;
    unsigned int expected[4] = {0x111222, 0x2333, 0x114514, 0x82813158};

    for (int i = 0; i < 4; i++) {
        *(unsigned int*)(mem + 11) = expected[i];
        asm volatile (
            "lw %0, 11(%1)"
            : "=r" (a)
            : "r" (mem)
        );
        check(a == expected[i]);
    }

    for (int i = 0; i < 4; i++) {
        *(unsigned int*)(mem + 3) = expected[i];
        asm volatile (
            "lw %0, 3(%1)"
            : "=r" (a)
            : "r" (mem)
        );
        check(a == expected[i]);
    }

    for (int i = 0; i < 4; i++) {
        *(unsigned int*)(mem + 21) = expected[i];
        asm volatile (
            "lw %0, 21(%1)"
            : "=r" (a)
            : "r" (mem)
        );
        check(a == expected[i]);
    }

    asm volatile (
        "lw %0, 3(%1)"
        : "=r" (a)
        : "r" (mem)
    );
    check(a == 0x82813158);
    asm volatile (
        "lw %0, 11(%1)"
        : "=r" (a)
        : "r" (mem)
    );
    check(a == 0x82813158);

    return 0;
}
