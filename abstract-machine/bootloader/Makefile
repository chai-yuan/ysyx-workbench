NAME = bootloader
AM_SRCS  :=

LOAD_BIN    ?= test.bin
LOAD_TARGET ?= 0x80000000
LOAD_LEN    := $(shell stat -c "%s" $(LOAD_BIN))

SRCS = bootloader.S

include $(AM_HOME)/Makefile

LDFLAGS  = -z noexecstack --gc-sections -T $(AM_HOME)/bootloader/linker.ld -melf32lriscv
LDFLAGS += --defsym=_load_target=$(LOAD_TARGET) \
           --defsym=_load_len=$(LOAD_LEN)

bootloader: binary
	@echo ---- bootloader begin ----
	@echo binary file len:$(LOAD_LEN)
	@echo load to :$(LOAD_TARGET)
	$(shell cat $(IMAGE).bin $(LOAD_BIN) > /tmp/combined.bin)
	$(shell mv /tmp/combined.bin $(LOAD_BIN))
	@echo ---- bootloader end ----

