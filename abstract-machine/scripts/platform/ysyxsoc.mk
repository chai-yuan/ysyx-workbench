AM_SRCS := riscv/ysyxsoc/start.S \
           riscv/ysyxsoc/trm.c \
           riscv/ysyxsoc/ioe.c \
           riscv/ysyxsoc/timer.c \
           riscv/ysyxsoc/input.c \
           riscv/ysyxsoc/cte.c \
           riscv/ysyxsoc/trap.S

CFLAGS    += -fdata-sections -ffunction-sections
LDFLAGS   += -T $(AM_HOME)/scripts/soclinker.ld

LDFLAGS   += --gc-sections
CFLAGS += -DMAINARGS=\"$(mainargs)\"
.PHONY: $(AM_HOME)/am/src/riscv/ysyxsoc/trm.c

SOCFLAGS += -d $(NEMU_HOME)/build/riscv32-nemu-interpreter-so
SOCFLAGS +=

image: binary
	@echo + ADD bootloader "->" $(IMAGE_REL).bin
	@$(MAKE) -C $(AM_HOME)/bootloader ARCH=riscv32-ysyxsoc LOAD_BIN=$(IMAGE).bin LOAD_TARGET=0xa0000000 bootloader

binary: $(IMAGE).elf
	@$(OBJDUMP) -d -M no-aliases $(IMAGE).elf > $(IMAGE).txt
	@echo + OBJCOPY "->" $(IMAGE_REL).bin
	@$(OBJCOPY) -S --set-section-flags .bss=alloc,contents -O binary $(IMAGE).elf $(IMAGE).bin

run: image
	$(MAKE) -C $(SOC_HOME) ISA=$(ISA) run ARGS="$(SOCFLAGS)" IMG=$(IMAGE).bin

