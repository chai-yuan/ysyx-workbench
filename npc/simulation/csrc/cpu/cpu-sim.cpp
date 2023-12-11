#include <cpu/cpu.h>
#include <cpu/difftest.h>
#include <cpu/sim.h>
#include <memory/paddr.h>
#include <trace.h>

VCPUTop* sim_cpu;
VerilatedContext* contextp;
VerilatedVcdC* tfp;

word_t clk_cycle, valid_cycle;
word_t inst;

void sim_init() {
    Log("sim init");

    contextp = new VerilatedContext();
    tfp = new VerilatedVcdC();
    sim_cpu = new VCPUTop();
    IFDEF(CONFIG_VTRACE, vtrace_init("debug.vcd"));
    clk_cycle = 0;
    inst = 0;

    sim_reset();

    update_cpu_state();

    Log("sim init end, pc = 0x%x", cpu.pc);
}

void sim_reset() {
    sim_cpu->clock = 0;
    sim_cpu->reset = 1;
    sim_cpu->eval();
    IFDEF(CONFIG_VTRACE, dump_wave());
    sim_cpu->clock = 1;
    sim_cpu->reset = 1;
    sim_cpu->eval();
    IFDEF(CONFIG_VTRACE, dump_wave());
    sim_cpu->clock = 0;
    sim_cpu->reset = 1;
    sim_cpu->eval();
    IFDEF(CONFIG_VTRACE, dump_wave());

    sim_cpu->clock = 1;
    sim_cpu->reset = 0;
    sim_cpu->eval();
    IFDEF(CONFIG_VTRACE, dump_wave());
}

void sim_exit() {
    IFDEF(CONFIG_VTRACE, vtrace_exit());
    Log("sim exit");
}

void sim_exec() {
    sim_cpu->clock = 0;
    sim_cpu->eval();
    IFDEF(CONFIG_VTRACE, dump_wave());

    sim_cpu->clock = 1;
    sim_cpu->eval();
    IFDEF(CONFIG_VTRACE, dump_wave());

    clk_cycle++;

    if (sim_cpu->io_debug_validInst) {
        update_cpu_state();
        valid_cycle++;

#ifdef CONFIG_DIFFTEST
        if (cpu.pc == 0x80000000 || sim_cpu->io_debug_skipIO)
            difftest_skip_ref();
        difftest_step(cpu.pc, 0);
#endif

        if (sim_cpu->io_debug_halt) {
            set_npc_state(NPC_END, cpu.pc, cpu.gpr[10]);
        }
    }
}

void update_cpu_state() {
    cpu.pc = sim_cpu->io_debug_pc;
    if (sim_cpu->io_debug_regWen && sim_cpu->io_debug_regWaddr != 0) {
        cpu.gpr[sim_cpu->io_debug_regWaddr] = sim_cpu->io_debug_regWdata;
    }
}
