#include <dlfcn.h>

#include <cpu/cpu.h>
#include <cpu/difftest.h>
#include <cpu/reg.h>
#include <memory/paddr.h>
#include <utils.h>

void (*ref_difftest_memcpy)(paddr_t addr,
                            void* buf,
                            size_t n,
                            bool direction) = NULL;
void (*ref_difftest_regcpy)(void* dut, bool direction) = NULL;
void (*ref_difftest_exec)(uint64_t n) = NULL;
void (*ref_difftest_raise_intr)(uint64_t NO) = NULL;

#ifdef CONFIG_DIFFTEST

static bool is_skip_ref = false;
static int skip_dut_nr_inst = 0;

void difftest_skip_ref() {
    is_skip_ref = true;
    skip_dut_nr_inst = 0;
}

void difftest_skip_dut(int nr_ref, int nr_dut) {
    skip_dut_nr_inst += nr_dut;

    while (nr_ref-- > 0) {
        ref_difftest_exec(1);
    }
}

void init_difftest(char* ref_so_file, long img_size, int port) {
    assert(ref_so_file != NULL);

    void* handle;
    handle = dlopen(ref_so_file, RTLD_LAZY);
    Assert(handle, "can not open so_file");

    ref_difftest_memcpy = (void (*)(paddr_t, void*, size_t, bool))dlsym(
        handle, "difftest_memcpy");
    assert(ref_difftest_memcpy);

    ref_difftest_regcpy =
        (void (*)(void*, bool))dlsym(handle, "difftest_regcpy");
    assert(ref_difftest_regcpy);

    ref_difftest_exec = (void (*)(uint64_t))dlsym(handle, "difftest_exec");
    assert(ref_difftest_exec);

    ref_difftest_raise_intr =
        (void (*)(uint64_t))dlsym(handle, "difftest_raise_intr");
    assert(ref_difftest_raise_intr);

    void (*ref_difftest_init)(int) =
        (void (*)(int))dlsym(handle, "difftest_init");
    assert(ref_difftest_init);

    Log("Differential testing: %s", ANSI_FMT("ON", ANSI_FG_GREEN));
    Log("The result of every instruction will be compared with %s. "
        "This will help you a lot for debugging, but also significantly reduce "
        "the performance. "
        "If it is not necessary, you can turn it off in config.h",
        ref_so_file);

    ref_difftest_init(port);
    ref_difftest_memcpy(RESET_VECTOR, guest_to_host(RESET_VECTOR), img_size,
                        DIFFTEST_TO_REF);

    // cpu init
    cpu.pc = RESET_VECTOR;
    ref_difftest_regcpy(&cpu, DIFFTEST_TO_REF);
}

static bool isa_difftest_checkregs(CPU_regs* ref, vaddr_t pc) {
    for (int i = 0; i < 32; i++) {
        if (cpu.gpr[i] != ref->gpr[i]) {
            printf("reg: x%d, NPC: 0x%x, NEMU: 0x%x\n", i, cpu.gpr[i],
                   ref->gpr[i]);
            return false;
        }
    }
    if (cpu.pc != ref->pc) {
        printf("PC: NPC: 0x%x, NEMU: 0x%x\n", cpu.pc, ref->pc);
        return false;
    }

    return true;
}

static void checkregs(CPU_regs* ref, vaddr_t pc) {
    if (!isa_difftest_checkregs(ref, pc)) {
        npc_state.state = NPC_ABORT;
        npc_state.halt_pc = pc;
        Log("NPC reg:");
        isa_reg_display();

        Log("NEMU reg:");
        ref_difftest_regcpy(&cpu, DIFFTEST_TO_DUT);
        isa_reg_display();
    }
}

void difftest_step(vaddr_t pc, vaddr_t npc) {
    CPU_regs ref_r;

    if (skip_dut_nr_inst > 0) {
        panic();

        ref_difftest_regcpy(&ref_r, DIFFTEST_TO_DUT);
        if (ref_r.pc == npc) {
            skip_dut_nr_inst = 0;
            checkregs(&ref_r, npc);
            return;
        }
        skip_dut_nr_inst--;
        if (skip_dut_nr_inst == 0)
            panic("can not catch up with ref.pc = " FMT_WORD
                  " at pc = " FMT_WORD,
                  ref_r.pc, pc);
        return;
    }

    if (is_skip_ref) {
        // to skip the checking of an instruction, just copy the reg state to
        // reference design
        ref_difftest_regcpy(&cpu, DIFFTEST_TO_REF);
        is_skip_ref = false;
        return;
    }

    ref_difftest_exec(1);
    ref_difftest_regcpy(&ref_r, DIFFTEST_TO_DUT);

    checkregs(&ref_r, pc);
}
#else
void init_difftest(char* ref_so_file, long img_size, int port) {}
#endif
