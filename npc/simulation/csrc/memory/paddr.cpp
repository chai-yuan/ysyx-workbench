#include <cpu/cpu.h>
#include <device/device.h>
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
                             0x00100073,
                             0x00100073};
    memcpy(pmem, build_in_img, sizeof(build_in_img));

    Log("physical memory area [" FMT_PADDR ", " FMT_PADDR "]", PMEM_LEFT, PMEM_RIGHT);
}

inline void mem_read(uint8_t* mem, int raddr, word_t* rdata) {
    int aligned_addr = raddr & ~0x3u;
    *rdata = *(uint32_t*)&mem[aligned_addr];
}

inline void mem_write(uint8_t* mem, int waddr, word_t wdata, char wmask) {
    int aligned_addr = waddr & ~0x3u;
    uint8_t* base_addr = &mem[aligned_addr];

    for (int i = 0; i < 4; i++) {
        if (wmask & (1 << i)) {
            base_addr[i] = wdata & 0xFF;
        }
        wdata >>= 8;
    }
}

void pmem_read(int raddr, word_t* rdata) {
    if (in_pmem(raddr)) {
        mem_read(pmem, raddr - CONFIG_MBASE, rdata);
        return;
    }
#ifdef CONFIG_DEVICE
    for (int i = 0; i < DEVICE_NUM; i++) {
        Device* device = &devices[i];
        if (device->low <= raddr && device->high > raddr) {
            mem_read(device->space, raddr - device->low, rdata);
            device->callback(raddr - device->low, false);
            return;
        }
    }
#endif
    out_of_bound(raddr);
}

void pmem_write(int waddr, word_t wdata, char wmask) {
    if (in_pmem(waddr)) {
        mem_write(pmem, waddr - CONFIG_MBASE, wdata, wmask);
        return;
    }
#ifdef CONFIG_DEVICE
    for (int i = 0; i < DEVICE_NUM; i++) {
        Device* device = &devices[i];
        if (device->low <= waddr && device->high > waddr) {
            mem_write(device->space, waddr - devices->low, wdata, wmask);
            device->callback(waddr - device->low, true);
            return;
        }
    }
#endif
    out_of_bound(waddr);
}
