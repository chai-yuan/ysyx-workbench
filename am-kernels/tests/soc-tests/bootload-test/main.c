#include "trap.h"

char a = 0x23;

int main() {
    printf("%p\n",&a);
    check(a == 0x23);
    a = 0x53;
    check(a == 0x53);

    // 通过测试
    halt(0);
}
