AM_SRCS := riscv/ysyxsoc/bootloader.S \
           riscv/ysyxsoc/start.S \
           riscv/ysyxsoc/trm.c \
           riscv/ysyxsoc/ioe.c \
           riscv/ysyxsoc/timer.c \
           riscv/ysyxsoc/input.c

CFLAGS    += -fdata-sections -ffunction-sections
LDFLAGS   += -T $(AM_HOME)/scripts/soclinker.ld

LDFLAGS   += --gc-sections
CFLAGS += -DMAINARGS=\"$(mainargs)\"
.PHONY: $(AM_HOME)/am/src/riscv/ysyxsoc/trm.c

SOCFLAGS += -d $(NEMU_HOME)/build/riscv32-nemu-interpreter-so
SOCFLAGS += -b

image: $(IMAGE).elf
	@$(OBJDUMP) -d -M no-aliases $(IMAGE).elf > $(IMAGE).txt
	@echo + OBJCOPY "->" $(IMAGE_REL).bin
	@$(OBJCOPY) -S --set-section-flags .bss=alloc,contents -O binary $(IMAGE).elf $(IMAGE).bin

run: image
	$(MAKE) -C $(SOC_HOME) ISA=$(ISA) run ARGS="$(SOCFLAGS)" IMG=$(IMAGE).bin

