/***************************************************************************************
 * Copyright (c) 2014-2022 Zihao Yu, Nanjing University
 *
 * NEMU is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan
 *PSL v2. You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 *
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY
 *KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 *NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *
 * See the Mulan PSL v2 for more details.
 ***************************************************************************************/
#include <cpu/cpu.h>
#include <cpu/decode.h>
#include <cpu/difftest.h>
#include <cpu/ifetch.h>
#include <locale.h>
#include <utils.h>
#include "../src/monitor/sdb/sdb.h"
/* The assembly code of instructions executed is only output to the screen
 * when the number of instructions executed is less than this value.
 * This is useful when you use the `si' command.
 * You can modify this value as you want.
 */
#define MAX_INST_TO_PRINT 4

CPU_state cpu = {};
uint64_t g_nr_guest_inst = 0;
static uint64_t g_timer = 0;  // unit: us
static bool g_print_step = false;

void device_update();

static void trace_and_difftest(Decode* _this, vaddr_t dnpc) {
    // #ifdef CONFIG_ITRACE_COND
    //     if (ITRACE_COND) {
    //         log_write("%s\n", _this->logbuf);
    //     }
    // #endif
    //     if (g_print_step) {
    //         IFDEF(CONFIG_ITRACE, puts(_this->logbuf));
    //     }
    bool watch_point_check = false;
    IFDEF(CONFIG_WATCHPOINT, check_wp(dnpc, &watch_point_check));
    if (watch_point_check) {
        nemu_state.state = NEMU_STOP;
    }
#ifdef CONFIG_DIFFTEST
    if (_this->intr != 0) {
        ref_difftest_raise_intr(_this->intr);
        if (_this->access_addr >= 0x2000000 &&
            _this->access_addr < 0x12000000) {
            ref_difftest_regcpy(&cpu, DIFFTEST_TO_REF);
        }
    } else {
        if (_this->access_addr >= 0x2000000 &&
            _this->access_addr < 0x12000000) {
            ref_difftest_regcpy(&cpu, DIFFTEST_TO_REF);
        } else {
            ref_difftest_exec(1);
        }
    }
    riscv32_CPU_state ref;
    ref_difftest_regcpy(&ref, DIFFTEST_TO_DUT);
    if (!isa_difftest_checkregs(&ref, cpu.pc)) {
        nemu_state.state = NEMU_ABORT;
        nemu_state.halt_pc = cpu.pc;
        isa_reg_display();
        ref_difftest_regcpy(&cpu, DIFFTEST_TO_DUT);
        isa_reg_display();
    }
#endif
}

/**
 * @brief Execute a single instruction in the simulator.
 *
 * This function executes a single instruction in the simulator. It first sets
 * the program counter to the given value, then executes the instruction, and
 * finally updates the program counter to the next instruction. If CONFIG_ITRACE
 * is defined, it also generates and prints a trace log of the instruction.
 *
 * @param s Pointer to the decode structure.
 * @param pc The current value of the program counter.
 */
static void exec_once(Decode* s, vaddr_t pc) {
    s->pc = pc;
    s->snpc = pc;
    isa_exec_once(s);
    cpu.pc = s->dnpc;
#ifdef CONFIG_ITRACE
    char* p = s->logbuf;
    p += snprintf(p, sizeof(s->logbuf), FMT_WORD ":", s->pc);
    int ilen = s->snpc - s->pc;
    int i;
    uint8_t* inst = (uint8_t*)&s->isa.inst.val;
    for (i = ilen - 1; i >= 0; i--) {
        p += snprintf(p, 4, " %02x", inst[i]);
    }
    int ilen_max = MUXDEF(CONFIG_ISA_x86, 8, 4);
    int space_len = ilen_max - ilen;
    if (space_len < 0)
        space_len = 0;
    space_len = space_len * 3 + 1;
    memset(p, ' ', space_len);
    p += space_len;

#ifndef CONFIG_ISA_loongarch32r
    void disassemble(char* str, int size, uint64_t pc, uint8_t* code,
                     int nbyte);
    disassemble(p, s->logbuf + sizeof(s->logbuf) - p,
                MUXDEF(CONFIG_ISA_x86, s->snpc, s->pc),
                (uint8_t*)&s->isa.inst.val, ilen);
#else
    p[0] = '\0';  // the upstream llvm does not support loongarch32r
#endif
#endif
}

static void execute(uint64_t n) {
    Decode s;
    for (; n > 0; n--) {
        exec_once(&s, cpu.pc);
        g_nr_guest_inst++;
        trace_and_difftest(&s, cpu.pc);
        if (nemu_state.state != NEMU_RUNNING)
            break;
        IFDEF(CONFIG_DEVICE, device_update());
    }
}

static void statistic() {
    IFNDEF(CONFIG_TARGET_AM, setlocale(LC_NUMERIC, ""));
#define NUMBERIC_FMT MUXDEF(CONFIG_TARGET_AM, "%", "%'") PRIu64
    Log("host time spent = " NUMBERIC_FMT " us", g_timer);
    Log("total guest instructions = " NUMBERIC_FMT, g_nr_guest_inst);
    if (g_timer > 0)
        Log("simulation frequency = " NUMBERIC_FMT " inst/s",
            g_nr_guest_inst * 1000000 / g_timer);
    else
        Log("Finish running in less than 1 us and can not calculate the "
            "simulation frequency");
}

void assert_fail_msg() {
    itrace_print();
}

/* Simulate how the CPU works. */
void cpu_exec(uint64_t n) {
    g_print_step = (n < MAX_INST_TO_PRINT);
    switch (nemu_state.state) {
        case NEMU_END:
        case NEMU_ABORT:
            printf(
                "Program execution has ended. To restart the program, exit "
                "NEMU and run again.\n");
            return;
        default:
            nemu_state.state = NEMU_RUNNING;
    }

    uint64_t timer_start = get_time();

    execute(n);

    uint64_t timer_end = get_time();
    g_timer += timer_end - timer_start;

    switch (nemu_state.state) {
        case NEMU_RUNNING:
            nemu_state.state = NEMU_STOP;
            break;

        case NEMU_END:
        case NEMU_ABORT:
            Log("nemu: %s at pc = " FMT_WORD,
                (nemu_state.state == NEMU_ABORT
                     ? ANSI_FMT("ABORT", ANSI_FG_RED)
                     : (nemu_state.halt_ret == 0
                            ? ANSI_FMT("HIT GOOD TRAP", ANSI_FG_GREEN)
                            : ANSI_FMT("HIT BAD TRAP", ANSI_FG_RED))),
                nemu_state.halt_pc);
            assert_fail_msg();
            // fall through
        case NEMU_QUIT:
            statistic();
    }
}
