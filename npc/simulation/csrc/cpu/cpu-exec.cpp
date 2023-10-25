#include <common.h>
#include <cpu/cpu.h>
#include <cpu/reg.h>
#include <cpu/sim.h>

CPU_regs cpu;

static void exec_once() {
    sim_exec();

#ifdef CONFIG_ITRACE
    char logbuf[128] = {0};
    char* p = logbuf;
    int ilen = 4;
    int i;
    uint8_t* now_inst = (uint8_t*)&inst;
    for (i = ilen - 1; i >= 0; i--) {
        p += snprintf(p, 4, " %02x", now_inst[i]);
    }

#ifdef CONFIG_DISASM
    int ilen_max = MUXDEF(CONFIG_ISA_x86, 8, 4);
    int space_len = ilen_max - ilen;
    if (space_len < 0)
        space_len = 0;
    space_len = space_len * 3 + 1;
    memset(p, ' ', space_len);
    p += space_len;

    void disassemble(char* str, int size, uint64_t pc, uint8_t* code, int nbyte);
    disassemble(p, logbuf + sizeof(logbuf) - p,
                MUXDEF(CONFIG_ISA_x86, cpu.pc + 4, cpu.pc), (uint8_t*)&inst, ilen);
#endif

    printf("%s\n", logbuf);
#endif
}

void cpu_exec(uint64_t n) {
    switch (npc_state.state) {
        case NPC_END:
        case NPC_ABORT:
            printf("Program execution has ended. To restart the program, exit NPC and run again.\n");
            return;
        default:
            npc_state.state = NPC_RUNNING;
    }
    // ----
    for (; n > 0; n--) {
        exec_once();
        if (npc_state.state != NPC_RUNNING)
            break;
        // IFDEF(CONFIG_DEVICE, device_update());
    }
    // ----

    switch (npc_state.state) {
        case NPC_RUNNING:
            npc_state.state = NPC_STOP;
            break;
        case NPC_END:
        case NPC_ABORT:
            Log("npc: %s at pc = " FMT_WORD,
                (npc_state.state == NPC_ABORT ? ANSI_FMT("ABORT", ANSI_FG_RED) : (npc_state.halt_ret == 0 ? ANSI_FMT("HIT GOOD TRAP", ANSI_FG_GREEN) : ANSI_FMT("HIT BAD TRAP", ANSI_FG_RED))),
                npc_state.halt_pc);
            // fall through
        case NPC_QUIT:
            Log("npc end at cycle : %d", cycle_num);
            // statistic();
            break;
    }
}

void assert_fail_msg() {
    isa_reg_display();
}

void set_npc_state(int state, vaddr_t pc, int halt_ret) {
    //    difftest_skip_ref();
    npc_state.state = state;
    npc_state.halt_pc = pc;
    npc_state.halt_ret = halt_ret;
}
