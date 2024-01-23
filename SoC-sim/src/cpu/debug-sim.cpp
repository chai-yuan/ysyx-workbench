#include <cpu/cpu.h>
#include <cpu/difftest.h>
#include <cpu/sim.h>
#include <trace.h>

extern "C" void debug_sim_halt() {
    set_npc_state(NPC_END, cpu.pc, cpu.gpr[10]);
}

extern "C" void debug_update_cpu(int deviceAccess,
                                 int deviceAddr,
                                 int pc,
                                 int regWen,
                                 int regWaddr,
                                 int regWdata) {
    cpu.pc = pc;
    if (regWen && regWaddr != 0) {
        cpu.gpr[regWaddr] = regWdata;
    }
    sim_statistic.valid_cycle++;

#ifdef CONFIG_DIFFTEST
    if (deviceAccess && (0x10000000 <= deviceAddr && deviceAddr < 0x10020000)) {
        difftest_flush();
    } else {
        CPU_regs ref;
        difftest_step(&ref);
        difftest_checkregs(&ref);
    }
#endif
}
