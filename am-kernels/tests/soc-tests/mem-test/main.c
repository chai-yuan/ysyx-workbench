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
    test_mem((unsigned int*)(0xa1000000),(unsigned int*)(0xa100a100));
    test_mem((unsigned int*)(0xa2000000),(unsigned int*)(0xa200a100));
    test_mem((unsigned int*)(0xa3000000),(unsigned int*)(0xa300a100));
    test_mem((unsigned int*)(0xa4000000),(unsigned int*)(0xa400a100));
    test_mem((unsigned int*)(0xa5000000),(unsigned int*)(0xa500a100));
    test_mem((unsigned int*)(0xa6000000),(unsigned int*)(0xa600a100));
    test_mem((unsigned int*)(0xa7000000),(unsigned int*)(0xa700a100));

    test_mem((unsigned int*)(0xa1100000),(unsigned int*)(0xa110a000));
    test_mem((unsigned int*)(0xa2100000),(unsigned int*)(0xa210a000));
    test_mem((unsigned int*)(0xa3100000),(unsigned int*)(0xa310a000));
    test_mem((unsigned int*)(0xa4100000),(unsigned int*)(0xa410a000));
    test_mem((unsigned int*)(0xa5100000),(unsigned int*)(0xa510a000));
    test_mem((unsigned int*)(0xa6100000),(unsigned int*)(0xa610a000));
    test_mem((unsigned int*)(0xa7100000),(unsigned int*)(0xa710a000));
    halt(0);
}
