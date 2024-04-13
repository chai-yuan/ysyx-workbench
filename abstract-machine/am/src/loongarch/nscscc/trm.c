#include <am.h>
#include <klib-macros.h>
#include <klib.h>
#include "../loongarch32r.h"

#define npc_trap(code) while(1);

extern char _heap_start, _heap_end;

int main(const char* args);

Area heap = RANGE(&_heap_start, &_heap_end);
#ifndef MAINARGS
#define MAINARGS ""
#endif
static const char mainargs[] = MAINARGS;

void putch(char ch) {
}

void halt(int code) {
    npc_trap(code);
    while (1)
        ;
}

void _trm_init() {
    int ret = main(mainargs);
    halt(ret);
}
