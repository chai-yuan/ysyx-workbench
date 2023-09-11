#include <common.h>
#include <utils.h>

#define itrace_buf_len 32

static InstItem iringbuf[itrace_buf_len];
static int iringbuf_p = 0;
static bool full = false;

void itrace_insert(paddr_t pc, uint32_t inst) {
    iringbuf[iringbuf_p].pc = pc;
    iringbuf[iringbuf_p].inst = inst;

    iringbuf_p++;
    if (iringbuf_p == itrace_buf_len) {
        full = true;
        iringbuf_p = 0;
    }
}

static void print_inst(vaddr_t pc, uint32_t inst_val, bool highlight) {
#ifdef CONFIG_ITRACE
    char print_buf[128];
    char* p = print_buf;
    if (highlight) {
        p += snprintf(p, sizeof(print_buf), "--->");
    } else {
        p += snprintf(p, sizeof(print_buf), "    ");
    }
    p += snprintf(p, sizeof(print_buf), FMT_WORD ":", pc);
    int ilen = 4;  // riscv
    int i;
    uint8_t* inst = (uint8_t*)&inst_val;
    for (i = ilen - 1; i >= 0; i--) {
        p += snprintf(p, 4, " %02x", inst[i]);
    }
    int ilen_max = MUXDEF(CONFIG_ISA_x86, 8, 4);
    int space_len = ilen_max - ilen;
    if (space_len < 0)
        space_len = 0;
    space_len = space_len * 3 + 1;
    memset(p, ' ', space_len);
    p += space_len;

#ifndef CONFIG_ISA_loongarch32r
    void disassemble(char* str, int size, uint64_t pc, uint8_t* code, int nbyte);
    disassemble(p, print_buf + sizeof(print_buf) - p,
                MUXDEF(CONFIG_ISA_x86, pc + 4, pc), (uint8_t*)&inst_val, ilen);
#else
    p[0] = '\0';  // the upstream llvm does not support loongarch32r
#endif
    printf("%s\n", print_buf);

#endif
}

void itrace_print() {
    if (full) {
        for (int i = 0; i < itrace_buf_len; i++) {
            int now_p = (iringbuf_p + i) % itrace_buf_len;
            print_inst(iringbuf[now_p].pc, iringbuf[now_p].inst, i == (itrace_buf_len - 1));
        }
    } else {
        for (int i = 0; i < iringbuf_p; i++) {
            print_inst(iringbuf[i].pc, iringbuf[i].inst, i == (iringbuf_p - 1));
        }
    }
}