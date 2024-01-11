#include <cpu/cpu.h>
#include <cpu/difftest.h>
#include <cpu/sim.h>
#include <trace.h>

extern "C" void debug_sim_halt() {
    set_npc_state(NPC_END, cpu.pc, cpu.gpr[10]);
}

extern "C" void debug_update_cpu(int pc,
                                 int regWen,
                                 int regWaddr,
                                 int regWdata) {
    cpu.pc = pc;
    if (regWen) {
        cpu.gpr[regWaddr] = regWdata;
    }

    sim_statistic.valid_cycle ++;
}
