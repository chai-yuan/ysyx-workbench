#include "trap.h"

extern char _heap_start, _heap_end;

int main() {
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

    // 通过测试
    halt(0);
}
