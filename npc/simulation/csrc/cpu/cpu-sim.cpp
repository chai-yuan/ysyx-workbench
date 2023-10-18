#include <cpu/cpu.h>
#include <cpu/difftest.h>
#include <cpu/sim.h>
#include <memory/mem-sim.h>
#include <memory/paddr.h>
#include <trace.h>

VSimCPUTop* sim_cpu;
VerilatedContext* contextp;
VerilatedVcdC* tfp;

word_t cycle_num;
word_t inst;

void sim_init() {
    Log("sim init");

    contextp = new VerilatedContext();
    tfp = new VerilatedVcdC();
    sim_cpu = new VSimCPUTop();
    IFDEF(CONFIG_VTRACE, vtrace_init("debug.vcd"));
    cycle_num = 0;
    inst = 0;

    sim_reset();

    update_cpu_state();

    Log("sim init end, pc = 0x%x", cpu.pc);
}

void sim_reset() {
    sim_cpu->clock = 0;
    sim_cpu->eval();
    sim_cpu->reset = 1;
    sim_cpu->eval();
    IFDEF(CONFIG_VTRACE, dump_wave());

    sim_cpu->clock = 1;
    sim_cpu->eval();
    sim_cpu->reset = 0;
    sim_cpu->eval();
    IFDEF(CONFIG_VTRACE, dump_wave());
}

void sim_exit() {
    IFDEF(CONFIG_VTRACE, vtrace_exit());
    Log("sim exit");
}

void sim_exec() {
    cycle_num++;

    sim_cpu->clock = 0;
    sim_cpu->eval();
    IFDEF(CONFIG_VTRACE, dump_wave());

    sim_cpu->clock = 1;
    sim_cpu->eval();
    IFDEF(CONFIG_VTRACE, dump_wave());

    update_cpu_state();
    // difftest
    if (cpu.pc != 0 && cpu.pc != 0x80000000) {
        IFDEF(CONFIG_DIFFTEST, difftest_step(cpu.pc, 0));
    }

    // halt
    if (sim_cpu->io_debug_halt) {
        set_npc_state(NPC_END, cpu.pc, cpu.gpr[10]);
    }
}

void update_cpu_state() {
    cpu.pc = sim_cpu->io_debug_pc;
    inst = sim_cpu->io_debug_inst;

    cpu.gpr[0] = sim_cpu->io_debug_regs_0;
    cpu.gpr[1] = sim_cpu->io_debug_regs_1;
    cpu.gpr[2] = sim_cpu->io_debug_regs_2;
    cpu.gpr[3] = sim_cpu->io_debug_regs_3;
    cpu.gpr[4] = sim_cpu->io_debug_regs_4;
    cpu.gpr[5] = sim_cpu->io_debug_regs_5;
    cpu.gpr[6] = sim_cpu->io_debug_regs_6;
    cpu.gpr[7] = sim_cpu->io_debug_regs_7;
    cpu.gpr[8] = sim_cpu->io_debug_regs_8;
    cpu.gpr[9] = sim_cpu->io_debug_regs_9;
    cpu.gpr[10] = sim_cpu->io_debug_regs_10;
    cpu.gpr[11] = sim_cpu->io_debug_regs_11;
    cpu.gpr[12] = sim_cpu->io_debug_regs_12;
    cpu.gpr[13] = sim_cpu->io_debug_regs_13;
    cpu.gpr[14] = sim_cpu->io_debug_regs_14;
    cpu.gpr[15] = sim_cpu->io_debug_regs_15;
    cpu.gpr[16] = sim_cpu->io_debug_regs_16;
    cpu.gpr[17] = sim_cpu->io_debug_regs_17;
    cpu.gpr[18] = sim_cpu->io_debug_regs_18;
    cpu.gpr[19] = sim_cpu->io_debug_regs_19;
    cpu.gpr[20] = sim_cpu->io_debug_regs_20;
    cpu.gpr[21] = sim_cpu->io_debug_regs_21;
    cpu.gpr[22] = sim_cpu->io_debug_regs_22;
    cpu.gpr[23] = sim_cpu->io_debug_regs_23;
    cpu.gpr[24] = sim_cpu->io_debug_regs_24;
    cpu.gpr[25] = sim_cpu->io_debug_regs_25;
    cpu.gpr[26] = sim_cpu->io_debug_regs_26;
    cpu.gpr[27] = sim_cpu->io_debug_regs_27;
    cpu.gpr[28] = sim_cpu->io_debug_regs_28;
    cpu.gpr[29] = sim_cpu->io_debug_regs_29;
    cpu.gpr[30] = sim_cpu->io_debug_regs_30;
    cpu.gpr[31] = sim_cpu->io_debug_regs_31;
}
