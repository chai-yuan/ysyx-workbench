#include <cpu/cpu.h>
#include <cpu/difftest.h>
#include <cpu/sim.h>
#include <memory/paddr.h>
#include <trace.h>

VysyxSoCFull* sim_soc;
VerilatedContext* contextp;
VerilatedVcdC* tfp;

word_t clk_cycle, valid_cycle;

void sim_init() {
    Log("sim init");

    contextp = new VerilatedContext();
    tfp = new VerilatedVcdC();
    sim_soc = new VysyxSoCFull();
    IFDEF(CONFIG_VTRACE, vtrace_init("debug.vcd"));
    clk_cycle = 0;

    sim_reset();

    Log("sim init end, pc = 0x%x", cpu.pc);
}

void sim_reset() {
    sim_soc->clock = 0;
    sim_soc->reset = 1;
    sim_soc->eval();
    IFDEF(CONFIG_VTRACE, dump_wave());
    sim_soc->clock = 1;
    sim_soc->reset = 1;
    sim_soc->eval();
    IFDEF(CONFIG_VTRACE, dump_wave());
    sim_soc->clock = 0;
    sim_soc->reset = 1;
    sim_soc->eval();
    IFDEF(CONFIG_VTRACE, dump_wave());

    sim_soc->clock = 1;
    sim_soc->reset = 0;
    sim_soc->eval();
    IFDEF(CONFIG_VTRACE, dump_wave());
}

void sim_exit() {
    IFDEF(CONFIG_VTRACE, vtrace_exit());
    Log("sim exit");
}

void sim_exec() {
    sim_soc->clock = 0;
    sim_soc->eval();
    IFDEF(CONFIG_VTRACE, dump_wave());

    sim_soc->clock = 1;
    sim_soc->eval();
    IFDEF(CONFIG_VTRACE, dump_wave());

    clk_cycle++;

//     if (sim_cpu->io_debug_validInst) {
//         valid_cycle++;
//         cpu.pc = sim_cpu->io_debug_pc;
// #ifdef CONFIG_DIFFTEST
//         if (cpu.pc != 0x80000000u) {
//             difftest_step(cpu.pc, 0);
//             if (sim_cpu->io_debug_skipIO)
//                 difftest_skip_ref();
//         }
// #endif
//         update_cpu_state();
//         if (sim_cpu->io_debug_halt) {
//             set_npc_state(NPC_END, cpu.pc, cpu.gpr[10]);
//         }
//     }

// #ifdef CONFIG_LOOP_CHECK
//     if (clk_cycle % 1000000 == 0) {
//         Log("now cycle : %u, inst : %u, pc : %x", clk_cycle, valid_cycle,
//             cpu.pc);
//     }
// #endif
}
