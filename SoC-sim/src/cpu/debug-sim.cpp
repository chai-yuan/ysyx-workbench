#include <cpu/cpu.h>
#include <cpu/difftest.h>
#include <cpu/sim.h>
#include <trace.h>

static bool sim_intr = false;

extern "C" void debug_sim_halt() {
    set_npc_state(NPC_END, cpu.pc, cpu.gpr[10]);
}

extern "C" void debug_sim_intr() {
    sim_intr = true;
}


extern "C" void debug_update_csr(int mstatus, int mcause, int mtvec, int mepc,
                                 int mscratch, int mie, int mip, int mtval) {
    cpu.mstatus = mstatus;
    cpu.mcause = mcause;
    cpu.mtvec = mtvec;
    cpu.mepc = mepc;
    cpu.mscratch = mscratch;
    cpu.mie = mie;
    cpu.mip = mip;
    cpu.mtval = mtval;
}

extern "C" void debug_update_reg(
    int reg0, int reg1, int reg2, int reg3, int reg4, int reg5, int reg6, int reg7, int reg8, int reg9,
    int reg10, int reg11, int reg12, int reg13, int reg14, int reg15, int reg16, int reg17, int reg18, int reg19,
    int reg20, int reg21, int reg22, int reg23, int reg24, int reg25, int reg26, int reg27, int reg28, int reg29,
    int reg30, int reg31
) {
    cpu.gpr[0] = reg0;
    cpu.gpr[1] = reg1;
    cpu.gpr[2] = reg2;
    cpu.gpr[3] = reg3;
    cpu.gpr[4] = reg4;
    cpu.gpr[5] = reg5;
    cpu.gpr[6] = reg6;
    cpu.gpr[7] = reg7;
    cpu.gpr[8] = reg8;
    cpu.gpr[9] = reg9;
    cpu.gpr[10] = reg10;
    cpu.gpr[11] = reg11;
    cpu.gpr[12] = reg12;
    cpu.gpr[13] = reg13;
    cpu.gpr[14] = reg14;
    cpu.gpr[15] = reg15;
    cpu.gpr[16] = reg16;
    cpu.gpr[17] = reg17;
    cpu.gpr[18] = reg18;
    cpu.gpr[19] = reg19;
    cpu.gpr[20] = reg20;
    cpu.gpr[21] = reg21;
    cpu.gpr[22] = reg22;
    cpu.gpr[23] = reg23;
    cpu.gpr[24] = reg24;
    cpu.gpr[25] = reg25;
    cpu.gpr[26] = reg26;
    cpu.gpr[27] = reg27;
    cpu.gpr[28] = reg28;
    cpu.gpr[29] = reg29;
    cpu.gpr[30] = reg30;
    cpu.gpr[31] = reg31;
}

extern "C" void debug_update_cpu(int deviceAccess,
                                 int deviceAddr,
                                 int pc
                                 ) {
    cpu.pc = pc;
    sim_statistic.valid_cycle++;

#ifdef CONFIG_DIFFTEST
    if (deviceAccess &&
        ((0x02000000 <= deviceAddr && deviceAddr < 0x02080000) ||
         (0x10000000 <= deviceAddr && deviceAddr < 0x10020000))) {
        difftest_flush();
    } else if(sim_intr) {
        ref_difftest_raise_intr(0x80000007);
    } else if (cpu.pc != CONFIG_PC) {
        CPU_regs ref;
        difftest_step(&ref);
        difftest_checkregs(&ref);
    }
#endif
}
