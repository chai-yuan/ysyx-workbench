AM_SRCS := riscv/ysyxsoc/start.S \
           riscv/ysyxsoc/trm.c \
           riscv/ysyxsoc/ioe/ioe.c \
           riscv/ysyxsoc/ioe/uart.c \
           riscv/ysyxsoc/ioe/timer.c \
           riscv/ysyxsoc/ioe/input.c \
           riscv/ysyxsoc/ioe/gpu.c \
           riscv/ysyxsoc/cte.c \
           riscv/ysyxsoc/vme.c \
           riscv/ysyxsoc/mpe.c \
           riscv/ysyxsoc/trap.S

CFLAGS    += -fdata-sections -ffunction-sections
LDFLAGS   += -T $(AM_HOME)/scripts/soclinker.ld

LDFLAGS   += --gc-sections
CFLAGS += -DMAINARGS=\"$(mainargs)\"
.PHONY: $(AM_HOME)/am/src/riscv/ysyxsoc/trm.c

SOCFLAGS += -d /home/charain/Project/mini-rv32ima/sim-nemu/mini-rv32ima.so
SOCFLAGS += -b

image: binary
	@echo + ADD bootloader "->" $(IMAGE_REL).bin
	@$(MAKE) -C $(AM_HOME)/bootloader ARCH=riscv32-ysyxsoc LOAD_BIN=$(IMAGE).bin LOAD_TARGET=0x80000000 bootloader

binary: $(IMAGE).elf
	@$(OBJDUMP) -d -M no-aliases $(IMAGE).elf > $(IMAGE).txt
	@echo + OBJCOPY "->" $(IMAGE_REL).bin
	@$(OBJCOPY) -S --set-section-flags .bss=alloc,contents -O binary $(IMAGE).elf $(IMAGE).bin

run: image
	$(MAKE) -C $(SIM_HOME) ISA=$(ISA) run ARGS="$(SOCFLAGS)" IMG=$(IMAGE).bin

