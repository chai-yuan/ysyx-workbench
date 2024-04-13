#include <stdio.h>

int main() {
    unsigned int a = 0x114514ff;
    unsigned int b = 0x3fd4;
    unsigned int result;

    // amoadd.w
    __asm__ volatile (
        "amoadd.w %[res], %[op1], (%[op2])\n\t"
        : [res] "=r" (result)
        : [op1] "r" (b), [op2] "r" (&a)
        : "memory"
    );
    printf("amoadd.w: %d\n", result);

    // amoxor.w
    __asm__ volatile (
        "amoxor.w %[res], %[op1], (%[op2])\n\t"
        : [res] "=r" (result)
        : [op1] "r" (b), [op2] "r" (&a)
        : "memory"
    );
    printf("amoxor.w: %d\n", result);

    // amoand.w
    __asm__ volatile (
        "amoand.w %[res], %[op1], (%[op2])\n\t"
        : [res] "=r" (result)
        : [op1] "r" (b), [op2] "r" (&a)
        : "memory"
    );
    printf("amoand.w: %d\n", result);

    // amoor.w
    __asm__ volatile (
        "amoor.w %[res], %[op1], (%[op2])\n\t"
        : [res] "=r" (result)
        : [op1] "r" (b), [op2] "r" (&a)
        : "memory"
    );
    printf("amoor.w: %d\n", result);

    // amomin.w
    __asm__ volatile (
        "amomin.w %[res], %[op1], (%[op2])\n\t"
        : [res] "=r" (result)
        : [op1] "r" (b), [op2] "r" (&a)
        : "memory"
    );
    printf("amomin.w: %d\n", result);

    // amomax.w
    __asm__ volatile (
        "amomax.w %[res], %[op1], (%[op2])\n\t"
        : [res] "=r" (result)
        : [op1] "r" (b), [op2] "r" (&a)
        : "memory"
    );
    printf("amomax.w: %d\n", result);

    // amominu.w
    __asm__ volatile (
        "amominu.w %[res], %[op1], (%[op2])\n\t"
        : [res] "=r" (result)
        : [op1] "r" (b), [op2] "r" (&a)
        : "memory"
    );
    printf("amominu.w: %d\n", result);

    // amomaxu.w
    __asm__ volatile (
        "amomaxu.w %[res], %[op1], (%[op2])\n\t"
        : [res] "=r" (result)
        : [op1] "r" (b), [op2] "r" (&a)
        : "memory"
    );
    printf("amomaxu.w: %d\n", result);

    // load
    unsigned int value;
    __asm__ volatile (
        "lw %[val], (%[addr])\n\t"
        : [val] "=r" (value)
        : [addr] "r" (&a)
        : "memory"
    );
    printf("lw: %d\n", value);

    // sc.w
    unsigned int new_value = 0xabcdef;
    unsigned int success;
    __asm__ volatile (
        "sc.w %[succ], %[new], (%[addr])\n\t"
        : [succ] "=r" (success)
        : [new] "r" (new_value), [addr] "r" (&a)
        : "memory"
    );
    printf("sc.w success: %d\n", success);
    printf("sc.w new value: %d\n", a);

    // lr.w
    __asm__ volatile (
        "lr.w %[val], (%[addr])\n\t"
        : [val] "=r" (value)
        : [addr] "r" (&a)
        : "memory"
    );
    printf("lr.w: %d\n", value);

    // sc.w
    __asm__ volatile (
        "sc.w %[succ], %[new], (%[addr])\n\t"
        : [succ] "=r" (success)
        : [new] "r" (new_value), [addr] "r" (&a)
        : "memory"
    );
    printf("sc.w success: %d\n", success);
    printf("sc.w new value: %d\n", a);

    // amoadd.w
    __asm__ volatile (
        "amoadd.w %[res], %[op1], (%[op2])\n\t"
        : [res] "=r" (result)
        : [op1] "r" (b), [op2] "r" (&a)
        : "memory"
    );
    printf("amoadd.w: %d\n", result);

    // amoxor.w
    __asm__ volatile (
        "amoxor.w %[res], %[op1], (%[op2])\n\t"
        : [res] "=r" (result)
        : [op1] "r" (b), [op2] "r" (&a)
        : "memory"
    );
    printf("amoxor.w: %d\n", result);

    return 0;
}
