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
#ifdef CONFIG_DIFFTEST
    if (pc == CONFIG_PC) {  // 第一次执行指令，使用本地状态更新ref状态
        difftest_flush();
    } else {
        CPU_regs ref;
        difftest_step(&ref);
        difftest_checkregs(&ref);
    }
#endif
    if (regWen && regWaddr != 0) {
        cpu.gpr[regWaddr] = regWdata;
    }
    sim_statistic.valid_cycle++;
}
