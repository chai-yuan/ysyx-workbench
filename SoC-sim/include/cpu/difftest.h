#ifndef DIFFTEST_H_
#define DIFFTEST_H_

#include <common.h>

enum { DIFFTEST_TO_DUT,
       DIFFTEST_TO_REF };

void init_difftest(char* ref_so_file, long img_size, int port);

void difftest_skip_ref();

void difftest_step(vaddr_t pc, vaddr_t npc);

extern void (*ref_difftest_memcpy)(word_t addr, void* buf, size_t n, bool direction);
extern void (*ref_difftest_regcpy)(void* dut, bool direction);
extern void (*ref_difftest_exec)(uint64_t n);
extern void (*ref_difftest_raise_intr)(uint64_t NO);

#endif