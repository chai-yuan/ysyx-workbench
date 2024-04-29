AM_SRCS := riscv/npc/start.S \
           riscv/npc/trm.c \
           riscv/npc/ioe/ioe.c \
           riscv/npc/ioe/uart.c \
           riscv/npc/ioe/timer.c \
           riscv/npc/ioe/input.c \
           riscv/npc/ioe/gpu.c \
           riscv/npc/cte.c \
           riscv/npc/mpe.c \
           riscv/npc/trap.S

CFLAGS    += -fdata-sections -ffunction-sections
LDFLAGS   += -T $(AM_HOME)/scripts/linker.ld \
						 --defsym=_pmem_start=0x80000000 --defsym=_entry_offset=0x0
LDFLAGS   += --gc-sections -e _start
CFLAGS += -DMAINARGS=\"$(mainargs)\"
.PHONY: $(AM_HOME)/am/src/riscv/npc/trm.c

NPCFLAGS += -d /home/charain/Project/mini-rv32ima/sim-nemu/mini-rv32ima.so
NPCFLAGS += -b

image: $(IMAGE).elf
	@$(OBJDUMP) -d -M no-aliases $(IMAGE).elf > $(IMAGE).txt
	@echo + OBJCOPY "->" $(IMAGE_REL).bin
	@$(OBJCOPY) -S --set-section-flags .bss=alloc,contents -O binary $(IMAGE).elf $(IMAGE).bin
	
	
run: image
	$(MAKE) -C $(SIM_HOME) TOP_NAME=CRRVTop ISA=$(ISA) run ARGS="$(NPCFLAGS)" IMG=$(IMAGE).bin
