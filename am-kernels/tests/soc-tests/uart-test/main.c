#include "trap.h"

int main() {
    ioe_init();
    printf("uart test!\n");

    halt(0);
}
