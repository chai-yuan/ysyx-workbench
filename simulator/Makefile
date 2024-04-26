WORK_DIR := $(CURDIR)

all: bin

VSRCS_DIR 	:= $(WORK_DIR)/vsrc
CSRCS_DIR 	:= $(WORK_DIR)/csrc
INCLUDE_DIR 	:= $(WORK_DIR)/include
OBJ_DIR 	:= $(WORK_DIR)/obj_dir

VSRCS += $(shell find $(abspath $(VSRCS_DIR)) -name "*.v")
CSRCS += $(shell find $(abspath $(CSRCS_DIR)) -name "*.cpp" -or -name "*.c") 

# verilator
TOP_NAME ?= ysyxSoCFull

VERILATOR_FLAGS := -I$(VSRCS_DIR)/perip/uart16550/rtl -I$(VSRCS_DIR)/perip/spi/rtl
VERILATOR_FLAGS += --top $(TOP_NAME) -Wno-lint -Wno-style -Wno-UNUSED
VERILATOR_FLAGS += --trace --debug --cc --exe --build --no-timing --timescale "1ns/1ns"
BIN 		:= $(OBJ_DIR)/V$(TOP_NAME)

# gcc
CFLAGS 	:= -I$(INCLUDE_DIR) -I$(NVBOARD_HOME)/usr/include -g -Wall -Wextra -D$(TOP_NAME)
LDFLAGS := -ldl -lreadline
LDFLAGS += $(shell sdl2-config --libs) -lSDL2_image -lSDL2_ttf

IMG 	?=
ARGS 	?=

SIM_FLAGS += $(ARGS) $(IMG)

# rules for NVBoard
include $(NVBOARD_HOME)/scripts/nvboard.mk

bin:$(CSRCS) $(VSRCS) $(NVBOARD_ARCHIVE)
	@echo "makefile directory: $(WORK_DIR)"
	@echo "Top name : $(TOP_NAME)"
	verilator $(VERILATOR_FLAGS) $(CSRCS) $(VSRCS) $(NVBOARD_ARCHIVE) \
	$(addprefix -CFLAGS , $(CFLAGS)) \
	$(addprefix -LDFLAGS , $(LDFLAGS))

run:bin
	$(call git_commit, "simulation")
	$(BIN) $(SIM_FLAGS)

clean:
	rm -rf $(OBJ_DIR)

.PHONY:clean
include ../Makefile
