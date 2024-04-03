#include <stdio.h>
#include "trap.h"
unsigned char mem[32];

int main() {
    unsigned int a;
    unsigned int expected[8] = {342423, 123456, 789012, 345678, 901234, 567890, 123456, 789012};

    for (int i = 0; i < 8; i++) {
        *(unsigned int*)(mem + 11) = expected[i];
        asm volatile (
            "lw %0, 11(%1)"
            : "=r" (a)
            : "r" (mem)
        );
        check(a == expected[i]);
    }

    for (int i = 0; i < 8; i++) {
        *(unsigned int*)(mem + 3) = expected[i];
        asm volatile (
            "lw %0, 3(%1)"
            : "=r" (a)
            : "r" (mem)
        );
        check(a == expected[i]);
    }

    for (int i = 0; i < 8; i++) {
        *(unsigned int*)(mem + 21) = expected[i];
        asm volatile (
            "lw %0, 21(%1)"
            : "=r" (a)
            : "r" (mem)
        );
        check(a == expected[i]);
    }

    return 0;
}
