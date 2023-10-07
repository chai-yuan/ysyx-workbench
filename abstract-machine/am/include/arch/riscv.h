#ifndef ARCH_H__
#define ARCH_H__

#ifdef __riscv_e	// riscv32e
#define GPR1 gpr[15] // a5
struct Context {
  uintptr_t gpr[16], mcause, mstatus, mepc;
  void *pdir;
};
#else			// riscv32
#define GPR1 gpr[17] // a7
struct Context {
  uintptr_t gpr[32], mcause, mstatus, mepc;
  void *pdir;
};
#endif

#define GPR2 gpr[10]
#define GPR3 gpr[11]
#define GPR4 gpr[12]
#define GPRx gpr[10]

#endif
