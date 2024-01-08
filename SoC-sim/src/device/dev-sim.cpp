#include <cpu/cpu.h>
#include <cpu/difftest.h>
#include <cpu/sim.h>
#include <device.h>
#include <memory/paddr.h>
#include <trace.h>

extern "C" void debug_sim_halt() {
    set_npc_state(NPC_END, cpu.pc, cpu.gpr[10]);
}

extern "C" void flash_read(uint32_t addr, uint32_t* data) {
    assert(0);
}

