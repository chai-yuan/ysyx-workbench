#include "trap.h"

const char* flash = (char *)0x30000000;

int main() {
    printf("read from flash:\n");
    for(int i=0;i<13;i++){
        printf("%d : %d\n",i,flash[i]);
    }

    // 通过测试
    halt(0);
}
