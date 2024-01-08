#include <cpu/cpu.h>
#include <cpu/difftest.h>
#include <memory/paddr.h>
#include <device.h>

void init_mem() {
    mrom_init();
}

uint8_t* guest_to_host(paddr_t paddr) {
    return mrom_raw_data() + paddr - CONFIG_MBASE;
}

paddr_t host_to_guest(uint8_t* haddr) {
    return haddr - mrom_raw_data() + CONFIG_MBASE;
}

static void out_of_bound(paddr_t addr) {
    panic("address = " FMT_PADDR " is out of bound of pmem [" FMT_PADDR
          ", " FMT_PADDR "] at pc = " FMT_WORD,
          addr, PMEM_LEFT, PMEM_RIGHT, cpu.pc);
}

inline void mem_read(uint8_t* mem, int raddr, word_t* rdata) {
    *rdata = *(uint32_t*)&mem[raddr];
}

inline void mem_write(uint8_t* mem, int waddr, word_t wdata, char wmask) {
    uint8_t* base_addr = &mem[waddr];

    for (int i = 0; i < 4; i++) {
        if (wmask & (1 << i)) {
            base_addr[i] = wdata & 0xFF;
        }
        wdata >>= 8;
    }
}

void pmem_read(int raddr, word_t* rdata) {
    raddr = raddr & ~0x3u;
    if (in_pmem(raddr)) {
        return;
    }
#ifdef CONFIG_DEVICE
    if (check_serial_addr(raddr)) {
        serial_read(raddr, (int*)rdata);
        return;
    }
    if (check_timer_addr(raddr)) {
        timer_read(raddr, (int*)rdata);
        return;
    }
#endif
    out_of_bound(raddr);
}

void pmem_write(int waddr, word_t wdata, char wmask) {
    waddr = waddr & ~0x3u;
    if (in_pmem(waddr)) {
        return;
    }
#ifdef CONFIG_DEVICE
    if (check_serial_addr(waddr)) {
        serial_write(waddr, wdata, wmask);
        return;
    }
    if (check_timer_addr(waddr)) {
        timer_write(waddr, wdata, wmask);
        return;
    }
#endif
    out_of_bound(waddr);
}
