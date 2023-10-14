#include <cpu/cpu.h>
#include <memory/paddr.h>

static uint8_t* pmem = NULL;

uint8_t* guest_to_host(paddr_t paddr) {
    return pmem + paddr - CONFIG_MBASE;
}
paddr_t host_to_guest(uint8_t* haddr) {
    return haddr - pmem + CONFIG_MBASE;
}

static void out_of_bound(paddr_t addr) {
    panic("address = " FMT_PADDR " is out of bound of pmem [" FMT_PADDR ", " FMT_PADDR "] at pc = " FMT_WORD,
          addr, PMEM_LEFT, PMEM_RIGHT, cpu.pc);
}

void init_mem() {
    pmem = (uint8_t*)malloc(CONFIG_MSIZE);
    assert(pmem);

    word_t build_in_img[] = {0x00009117,  // auipc x2,0x9
                             0x00300193,  // addi x3, x0, 3
                             0x00200213,  // addi x4,x0,2
                             0x404182b3,  // sub x5, x3, x4
                             0x00000000};
    memcpy(pmem, build_in_img, sizeof(build_in_img));

    Log("physical memory area [" FMT_PADDR ", " FMT_PADDR "]", PMEM_LEFT, PMEM_RIGHT);
}

void pmem_read(int raddr, word_t* rdata) {
    if (in_pmem(raddr)) {
        int aligned_addr = raddr & ~0x3u;
        *rdata = *(uint32_t*)guest_to_host(aligned_addr);
    }
#ifdef CONFIG_DEVICE

#endif
    else {
        out_of_bound(raddr);
    }
}

void pmem_write(int waddr, word_t wdata, char wmask) {
    if (in_pmem(waddr)) {
        int aligned_addr = waddr & ~0x3u;
        uint8_t* base_addr = guest_to_host(aligned_addr);

        for (int i = 0; i < 4; i++) {
            if (wmask & (1 << i)) {
                base_addr[i] = wdata & 0xFF;
            }
            wdata >>= 8;
        }
    }
#ifdef CONFIG_DEVICE

#endif
    else {
        out_of_bound(waddr);
    }
}
