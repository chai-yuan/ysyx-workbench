#include <cpu/cpu.h>
#include <cpu/sim.h>
#include <memory/paddr.h>
#include <trace.h>

VCPUTop* cpu_top;
VerilatedContext* contextp;
VerilatedVcdC* tfp;

word_t cycle_num;
word_t inst;

void sim_init() {
    Log("sim init");

    contextp = new VerilatedContext();
    tfp = new VerilatedVcdC();
    cpu_top = new VCPUTop();
    IFDEF(CONFIG_VTRACE, vtrace_init("debug.vcd"));
    cycle_num = 0;
    inst = 0;

    sim_reset();

    update_regs();

    Log("sim init end, pc = 0x%x", cpu.pc);
}

void sim_reset() {
    cpu_top->clock = 0;
    cpu_top->eval();
    cpu_top->reset = 1;
    cpu_top->eval();
    IFDEF(CONFIG_VTRACE, dump_wave());

    cpu_top->clock = 1;
    cpu_top->eval();
    cpu_top->reset = 0;
    cpu_top->eval();
    IFDEF(CONFIG_VTRACE, dump_wave());
}

void sim_exit() {
    IFDEF(CONFIG_VTRACE, vtrace_exit());
    Log("sim exit");
}

void sim_exec() {
    cycle_num++;

    cpu_top->clock = 0;
    cpu_top->eval();
    sim_mem();
    IFDEF(CONFIG_VTRACE, dump_wave());

    cpu_top->clock = 1;
    cpu_top->eval();
    IFDEF(CONFIG_VTRACE, dump_wave());

    update_regs();

    // halt
    if (cpu_top->io_debug_decode_ebreak) {
        set_npc_state(NPC_END, cpu.pc, cpu.gpr[10]);
    }
}

void sim_mem() {
    paddr_t dataAddr = cpu_top->io_dataSRAM_addr;
    int dataWe = cpu_top->io_dataSRAM_we;

    if (cpu_top->io_dataSRAM_we) {
        cpu_top->io_dataSRAM_rdata = paddr_read(dataAddr, cpu_top->io_dataSRAM_we);
        if (cpu_top->io_dataSRAM_en) {
            paddr_write(dataAddr, cpu_top->io_dataSRAM_we, cpu_top->io_dataSRAM_wdata);
        }
    }

    cpu_top->eval();

    paddr_t instAddr = cpu_top->io_instSRAM_addr;
    inst = paddr_read(instAddr, 4);
    cpu_top->io_instSRAM_rdata = inst;

    cpu_top->eval();
}

void update_regs() {
    cpu.last_pc = cpu.pc;
    cpu.pc = cpu_top->io_debug_pc;

    cpu.gpr[0] = cpu_top->io_debug_decode_regs_0;
    cpu.gpr[1] = cpu_top->io_debug_decode_regs_1;
    cpu.gpr[2] = cpu_top->io_debug_decode_regs_2;
    cpu.gpr[3] = cpu_top->io_debug_decode_regs_3;
    cpu.gpr[4] = cpu_top->io_debug_decode_regs_4;
    cpu.gpr[5] = cpu_top->io_debug_decode_regs_5;
    cpu.gpr[6] = cpu_top->io_debug_decode_regs_6;
    cpu.gpr[7] = cpu_top->io_debug_decode_regs_7;
    cpu.gpr[8] = cpu_top->io_debug_decode_regs_8;
    cpu.gpr[9] = cpu_top->io_debug_decode_regs_9;
    cpu.gpr[10] = cpu_top->io_debug_decode_regs_10;
    cpu.gpr[11] = cpu_top->io_debug_decode_regs_11;
    cpu.gpr[12] = cpu_top->io_debug_decode_regs_12;
    cpu.gpr[13] = cpu_top->io_debug_decode_regs_13;
    cpu.gpr[14] = cpu_top->io_debug_decode_regs_14;
    cpu.gpr[15] = cpu_top->io_debug_decode_regs_15;
    cpu.gpr[16] = cpu_top->io_debug_decode_regs_16;
    cpu.gpr[17] = cpu_top->io_debug_decode_regs_17;
    cpu.gpr[18] = cpu_top->io_debug_decode_regs_18;
    cpu.gpr[19] = cpu_top->io_debug_decode_regs_19;
    cpu.gpr[20] = cpu_top->io_debug_decode_regs_20;
    cpu.gpr[21] = cpu_top->io_debug_decode_regs_21;
    cpu.gpr[22] = cpu_top->io_debug_decode_regs_22;
    cpu.gpr[23] = cpu_top->io_debug_decode_regs_23;
    cpu.gpr[24] = cpu_top->io_debug_decode_regs_24;
    cpu.gpr[25] = cpu_top->io_debug_decode_regs_25;
    cpu.gpr[26] = cpu_top->io_debug_decode_regs_26;
    cpu.gpr[27] = cpu_top->io_debug_decode_regs_27;
    cpu.gpr[28] = cpu_top->io_debug_decode_regs_28;
    cpu.gpr[29] = cpu_top->io_debug_decode_regs_29;
    cpu.gpr[30] = cpu_top->io_debug_decode_regs_30;
    cpu.gpr[31] = cpu_top->io_debug_decode_regs_31;
}