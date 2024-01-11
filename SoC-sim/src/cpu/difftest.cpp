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
    ref_difftest_memcpy(CONFIG_PC, guest_to_host(CONFIG_PC), img_size,
                        DIFFTEST_TO_REF);
}

static bool isa_difftest_checkregs(CPU_regs* ref) {
    for (int i = 1; i < 32; i++) {
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

void difftest_flush(){
    ref_difftest_regcpy(&cpu, DIFFTEST_TO_REF);
}

void difftest_checkregs(CPU_regs* ref) {
    if (!isa_difftest_checkregs(ref)) {
        npc_state.state = NPC_ABORT;
        npc_state.halt_pc = cpu.pc;
        Log("NPC reg:");
        isa_reg_display();

        Log("NEMU reg:");
        ref_difftest_regcpy(&cpu, DIFFTEST_TO_DUT);
        isa_reg_display();
    }
}

void difftest_step(CPU_regs* ref) {
    ref_difftest_exec(1);
    ref_difftest_regcpy(ref, DIFFTEST_TO_DUT);
}

#else
void init_difftest(char* ref_so_file, long img_size, int port) {}
#endif
