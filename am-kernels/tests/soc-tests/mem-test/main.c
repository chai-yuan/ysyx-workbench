#include "trap.h"

extern char _heap_start, _heap_end;

#define RAM_BEGIN 0xa3f00000
#define RAM_END   0xa4200000

void test_mem(unsigned int* mem_begin,unsigned int* mem_end){
    printf("--mem_test %p -> %p --\n",mem_begin,mem_end);
    // 四字节测试
    printf("4 bits write\n");
    for (unsigned int* p = mem_begin;(p + 1) <= mem_end; p++) {
        *p = (unsigned int)((uintptr_t)p ^ 0xFAFF33AF);
    }
    printf("4 bits read\n");
    for (unsigned int* p = mem_begin;(p + 1) <= mem_end; p++) {
        check(*p == (unsigned int)((uintptr_t)p ^ 0xFAFF33AF));
    }
}

int main() {
    // // 单字节测试
    // printf("1 bits write\n");
    // for (char* p = (char*)(RAM_BEGIN); p < (char*)(RAM_END); p++) {
    //     *p = (char)((uintptr_t)p ^ 0x4a);
    // }
    // printf("1 bits read\n");
    // for (char* p = (char*)(RAM_BEGIN); p < (char*)(RAM_END); p++) {
    //     check(*p == (char)((uintptr_t)p ^ 0x4a));
    // }
    // // 双字节测试
    // printf("2 bits write\n");
    // for (unsigned short* p = (unsigned short*)(RAM_BEGIN);
    //      (p + 1) <= (unsigned short *)(RAM_END); p++) {
    //     *p = (unsigned short)((uintptr_t)p ^ 0xCFAF);
    // }
    // printf("2 bits read\n");
    // for (unsigned short* p = (unsigned short*)(RAM_BEGIN);
    //      (p + 1) <= (unsigned short *)(RAM_END); p++) {
    //     check(*p == (unsigned short)((uintptr_t)p ^ 0xCFAF));
    // }
    test_mem((unsigned int*)(0x81000000),(unsigned int*)(0x8100a100));
    test_mem((unsigned int*)(0x82000000),(unsigned int*)(0x8200a100));
    test_mem((unsigned int*)(0x83000000),(unsigned int*)(0x8300a100));
    test_mem((unsigned int*)(0x84000000),(unsigned int*)(0x8400a100));
    test_mem((unsigned int*)(0x85000000),(unsigned int*)(0x8500a100));
    test_mem((unsigned int*)(0x86000000),(unsigned int*)(0x8600a100));
    test_mem((unsigned int*)(0x87000000),(unsigned int*)(0x8700a100));

    test_mem((unsigned int*)(0x81100000),(unsigned int*)(0x8110a000));
    test_mem((unsigned int*)(0x82100000),(unsigned int*)(0x8210a000));
    test_mem((unsigned int*)(0x83100000),(unsigned int*)(0x8310a000));
    test_mem((unsigned int*)(0x84100000),(unsigned int*)(0x8410a000));
    test_mem((unsigned int*)(0x85100000),(unsigned int*)(0x8510a000));
    test_mem((unsigned int*)(0x86100000),(unsigned int*)(0x8610a000));
    test_mem((unsigned int*)(0x87100000),(unsigned int*)(0x8710a000));
    halt(0);
}
