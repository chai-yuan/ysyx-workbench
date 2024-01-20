#include "trap.h"

const char* flash = (char *)0x30000000;

int main() {
    void (*func_ptr)() = func_ptr = (void (*)())0x30000000;
    func_ptr();
    // 通过测试
    halt(0);
}
