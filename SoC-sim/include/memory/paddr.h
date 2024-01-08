#ifndef MEMORY_H_
#define MEMORY_H_

#include <common.h>

void init_mem();

uint8_t* guest_to_host(paddr_t paddr);

paddr_t host_to_guest(uint8_t* haddr);

inline void mem_read(uint8_t* mem, int raddr, word_t* rdata);

inline void mem_write(uint8_t* mem, int waddr, word_t wdata, char wmask);

void pmem_read(int raddr, word_t* rdata);

void pmem_write(int waddr, word_t wdata, char wmask);

#define PMEM_LEFT ((paddr_t)CONFIG_MBASE)
#define PMEM_RIGHT ((paddr_t)CONFIG_MBASE + CONFIG_MSIZE - 1)
#define RESET_VECTOR (PMEM_LEFT + CONFIG_PC_RESET_OFFSET)

static inline bool in_pmem(paddr_t addr) {
    return addr - CONFIG_MBASE < CONFIG_MSIZE;
}

#endif