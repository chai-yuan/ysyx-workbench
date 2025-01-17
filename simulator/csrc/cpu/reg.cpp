#include <common.h>
#include <cpu/reg.h>

const char* regs[] = {
    "$0", "ra", "sp", "gp", "tp", "t0", "t1", "t2",
    "s0", "s1", "a0", "a1", "a2", "a3", "a4", "a5",
    "a6", "a7", "s2", "s3", "s4", "s5", "s6", "s7",
    "s8", "s9", "s10", "s11", "t3", "t4", "t5", "t6"};

void isa_reg_display() {
    printf("pc : 0x%-10x\n", cpu.pc);
    for (int i = 0; i < 32; i += 2) {
        printf("x%-2d:%-6s: 0x%-10x %-12d | ", i, reg_name(i), gpr(i), gpr(i));
        if (i + 1 < 32) {
            printf("x%-2d:%-6s: 0x%-10x %-12d\n", i + 1, reg_name(i + 1), gpr(i + 1), gpr(i + 1));
        }
    }
}

word_t isa_reg_str2val(const char* s, bool* success) {
    *success = false;
    for (int i = 0; i < 32; i++) {
        if (strcmp(regs[i], s) == 0) {
            *success = true;
            return gpr(i);
        }
    }
    if (strcmp(s, "pc") == 0) {
        *success = true;
        return cpu.pc;
    }

    return -1;
}
