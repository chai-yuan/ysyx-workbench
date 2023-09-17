#include "syscall.h"
#include <common.h>

void do_syscall(Context* c) {
    uintptr_t a[4];
    a[0] = c->GPR1;

    switch (a[0]) {
        case SYS_exit:
            c->GPRx = sys_exit(c->GPR2);
            break;
        case SYS_yield:
            c->GPRx = sys_yield();
            break;
        default:
            panic("Unhandled syscall ID = %d", a[0]);
    }

    Log("syscall : %d(%d,%d) -> %d", a[0], c->GPR2, c->GPR3, c->GPRx);
}

int sys_yield() {
    yield();
    return 0;
}

int sys_exit(int code) {
    halt(code);
    return 0;
}