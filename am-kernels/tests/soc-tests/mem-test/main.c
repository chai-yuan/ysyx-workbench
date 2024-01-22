#include "trap.h"

// #define TEST_HEAP 1

#define TEST_PSRAM 1
#define PSRAM_BEGIN 0x80000000
#define PSRAM_END   0x80002000

extern char _heap_start, _heap_end;

int main() {

#ifdef TEST_PSRAM
    // 单字节测试
    for (char* p = (char*)(PSRAM_BEGIN); p < (char*)(PSRAM_END); p++) {
        *p = (char)((uintptr_t)p ^ 0x3D);
    }
    for (char* p = (char*)(PSRAM_BEGIN); p < (char*)(PSRAM_END); p++) {
        check(*p == (char)((uintptr_t)p ^ 0x3D));
    }
    // 双字节测试
    for (unsigned short* p = (unsigned short*)(PSRAM_BEGIN);
         (p + 1) <= (unsigned short *)(PSRAM_END); p++) {  
        *p = (unsigned short)((uintptr_t)p ^ 0xCFAF);
    }
    for (unsigned short* p = (unsigned short*)(PSRAM_BEGIN);
         (p + 1) <= (unsigned short *)(PSRAM_END); p++) {
        check(*p == (unsigned short)((uintptr_t)p ^ 0xCFAF));
    }

    // 四字节测试
    for (unsigned int* p = (unsigned int*)(PSRAM_BEGIN);
         (p + 1) <= (unsigned int*)(PSRAM_END); p++) {  
        *p = (unsigned int)((uintptr_t)p ^ 0xFAFF33AF);
    }
    for (unsigned int* p = (unsigned int*)(PSRAM_BEGIN);
         (p + 1) <= (unsigned int*)(PSRAM_END); p++) {
        check(*p == (unsigned int)((uintptr_t)p ^ 0xFAFF33AF));
    }
#endif

#ifdef TEST_HEAP
    // 单字节测试
    for (char* p = &_heap_start; p < &_heap_end; p++) {
        *p = (char)((uintptr_t)p ^ 0xFD);
    }
    for (char* p = &_heap_start; p < &_heap_end; p++) {
        check(*p == (char)((uintptr_t)p ^ 0xFD));
    }

    // 双字节测试
    for (unsigned short* p = (unsigned short*)&_heap_start;
         (char*)(p + 1) <= &_heap_end; p++) {  // 确保p + 1不会超出堆的末尾
        *p = (unsigned short)((uintptr_t)p ^ 0xCFAF);
    }
    for (unsigned short* p = (unsigned short*)&_heap_start;
         (char*)(p + 1) <= &_heap_end; p++) {
        check(*p == (unsigned short)((uintptr_t)p ^ 0xCFAF));
    }

    // 四字节测试
    for (unsigned int* p = (unsigned int*)&_heap_start;
         (char*)(p + 1) <= &_heap_end; p++) {  // 确保p + 1不会超出堆的末尾
        *p = (unsigned int)((uintptr_t)p ^ 0xFAFF33AF);
    }
    for (unsigned int* p = (unsigned int*)&_heap_start;
         (char*)(p + 1) <= &_heap_end; p++) {
        check(*p == (unsigned int)((uintptr_t)p ^ 0xFAFF33AF));
    }
#endif

    // 通过测试
    halt(0);
}
