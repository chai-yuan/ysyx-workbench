AM_SRCS := riscv/ysyxsoc/start.S \
           riscv/ysyxsoc/trm.c

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
	@$(OBJCOPY) -S --only-section=.text --only-section=.data --only-section=.rodata -O binary $(IMAGE).elf $(IMAGE).bin

run: image
	$(MAKE) -C $(SOC_HOME) ISA=$(ISA) run ARGS="$(SOCFLAGS)" IMG=$(IMAGE).bin

