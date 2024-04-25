#include <common.h>
#include <cpu/cpu.h>
#include <cpu/reg.h>
#include <sim/sim.h>

CPU_regs cpu;

static void exec_once() { sim_exec(); }

void cpu_exec(uint64_t n) {
    switch (npc_state.state) {
    case NPC_END:
    case NPC_ABORT:
        printf("Program execution has ended. To restart the program, exit NPC "
               "and run again.\n");
        return;
    default:
        npc_state.state = NPC_RUNNING;
    }
    // ----
    for (; n > 0; n--) {
        exec_once();
        if (npc_state.state != NPC_RUNNING)
            break;
    }
    // ----
    switch (npc_state.state) {
    case NPC_RUNNING:
        npc_state.state = NPC_STOP;
        break;
    case NPC_END:
    case NPC_ABORT:
        Log("npc: %s at pc = " FMT_WORD,
            (npc_state.state == NPC_ABORT
                 ? ANSI_FMT("ABORT", ANSI_FG_RED)
                 : (npc_state.halt_ret == 0
                        ? ANSI_FMT("HIT GOOD TRAP", ANSI_FG_GREEN)
                        : ANSI_FMT("HIT BAD TRAP", ANSI_FG_RED))),
            npc_state.halt_pc);
        // fall through
    case NPC_QUIT:
        statistic();
        break;
    }
}

void assert_fail_msg() { isa_reg_display(); }

void set_npc_state(int state, vaddr_t pc, int halt_ret) {
    npc_state.state = state;
    npc_state.halt_pc = pc;
    npc_state.halt_ret = halt_ret;
}

void statistic() {
    Log("----statistic----");
    Log("npc end at cycle : %d", sim_statistic.clock_cycle);
    Log("npc exec inst : %d", sim_statistic.valid_cycle);
    Log("npc ipc : %f",
        (float)sim_statistic.valid_cycle / (float)sim_statistic.clock_cycle);
}
