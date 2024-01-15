#include <cpu/cpu.h>
#include <cpu/difftest.h>
#include <device.h>
#include <memory/paddr.h>

static uint8_t* raw_mem;
paddr_t mem_base, mem_end;

// 配置需要使用的代码段储存器
void init_mem() {
    flash_init();
    raw_mem = mrom_raw_data();
    mem_base = MROM_BASE;
    mem_end = MROM_END;
}

uint8_t* guest_to_host(paddr_t paddr) {
    return raw_mem + paddr - mem_base;
}

paddr_t host_to_guest(uint8_t* haddr) {
    return haddr - raw_mem + mem_base;
}

static inline bool in_pmem(paddr_t addr) {
    return addr >= mem_base && addr < mem_end;
}

static void out_of_bound(paddr_t addr) {
    panic("address = " FMT_PADDR " is out of bound of pmem at pc = " FMT_WORD,
          addr, cpu.pc);
}

void pmem_read(int raddr, word_t* rdata) {
    raddr = raddr & ~0x3u;
    if (in_pmem(raddr)) {
        *rdata = *((word_t*)guest_to_host(raddr));
        return;
    }
    out_of_bound(raddr);
}

void pmem_write(int waddr, word_t wdata, char wmask) {
    waddr = waddr & ~0x3u;
    if (in_pmem(waddr)) {
        uint8_t* base_addr = guest_to_host(waddr);
        for (int i = 0; i < 4; i++) {
            if (wmask & (1 << i)) {
                base_addr[i] = wdata & 0xFF;
            }
            wdata >>= 8;
        }        
        return;
    }
    out_of_bound(waddr);
}
