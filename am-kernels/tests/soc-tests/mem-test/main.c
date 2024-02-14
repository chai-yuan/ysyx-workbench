#include "trap.h"

extern char _heap_start, _heap_end;

#define RAM_BEGIN 0xa3f00000
#define RAM_END   0xa4200000

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

    // 四字节测试
    printf("4 bits write\n");
    for (unsigned int* p = (unsigned int*)(RAM_BEGIN);
         (p + 1) <= (unsigned int*)(RAM_END); p++) {
        *p = (unsigned int)((uintptr_t)p ^ 0xFAFF33AF);
    }
    printf("4 bits read\n");
    for (unsigned int* p = (unsigned int*)(RAM_BEGIN);
         (p + 1) <= (unsigned int*)(RAM_END); p++) {
        check(*p == (unsigned int)((uintptr_t)p ^ 0xFAFF33AF));
    }

    halt(0);
}
