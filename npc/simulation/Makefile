VSRCS_DIR := $(NPC_HOME)/simulation/vsrc
CSRCS_DIR := $(NPC_HOME)/simulation/csrc
INCLUDE_DIR := $(NPC_HOME)/simulation/include

VSRCS += $(shell find $(abspath $(VSRCS_DIR)) -name "*.v")
CSRCS += $(shell find $(abspath $(CSRCS_DIR)) -name "*.cpp" -or -name "*.cc") 
HSRCS += $(shell find $(abspath $(INCLUDE_DIR)) -name "*.h")

# verilator
TOP_NAME := CPUTop

VERILATOR_FLAGS := -I$(VSRCS_DIR) --top $(TOP_NAME) -Wno-lint -Wno-style -Wno-UNUSED
VERILATOR_FLAGS += --trace --debug --cc --exe --build
OBJ_DIR := ./obj_dir
BIN := $(OBJ_DIR)/V$(TOP_NAME)

# gcc
CFLAGS := -I$(INCLUDE_DIR) -g -Wall -Wextra
LDFLAGS := -ldl
DIFFTEST_SO := TODO
IMG ?= TODO

RUN_FLAGS := --img=$(IMG) --diff=$(DIFFTEST_SO)

bin:$(CSRCS) $(VSRCS)
	verilator $(VERILATOR_FLAGS) $(CSRCS) $(VSRCS) \
	$(addprefix -CFLAGS , $(CFLAGS)) \
	$(addprefix -LDFLAGS , $(LDFLAGS))

run:bin
	$(call git_commit, "npc simulation")
	$(BIN)


clean:
	rm -rf $(OBJ_DIR)

.PHONY:clean
include ../../Makefile
