VSRCS_DIR := $(NPC_HOME)/simulation/vsrc
CSRCS_DIR := $(NPC_HOME)/simulation/csrc
INCLUDE_DIR := $(NPC_HOME)/simulation/include
OBJ_DIR := ./obj_dir

VSRCS += $(shell find $(abspath $(VSRCS_DIR)) -name "*.v")
CSRCS += $(shell find $(abspath $(CSRCS_DIR)) -name "*.cpp" -or -name "*.c") 
HSRCS += $(shell find $(abspath $(INCLUDE_DIR)) -name "*.h")
HSRCS += $(shell find $(abspath $(OBJ_DIR)) -name "*.h")

# llvm
LLVM_CXXFLAGS = $(shell llvm-config-11 --cxxflags)
LLVM_LIBS = $(shell llvm-config-11 --libs)

# verilator
TOP_NAME := CPUTop

VERILATOR_FLAGS := -I$(VSRCS_DIR) --top $(TOP_NAME) -Wno-lint -Wno-style -Wno-UNUSED
VERILATOR_FLAGS += --trace --debug --cc --exe --build
BIN := $(OBJ_DIR)/V$(TOP_NAME)

# gcc
CFLAGS := -I$(INCLUDE_DIR) -g -Wall -Wextra 
# CFLAGS += -fPIE $(LLVM_CXXFLAGS)
LDFLAGS := -ldl -lreadline
# LDFLAGS += $(LLVM_LIBS)

DIFFTEST_SO := NULL
IMG ?= NULL

NPC_FLAGS += $(IMG)
# NPC_FLAGS += --diff=$(DIFFTEST_SO)

bin:$(CSRCS) $(VSRCS)
	verilator $(VERILATOR_FLAGS) $(CSRCS) $(VSRCS) \
	$(addprefix -CFLAGS , $(CFLAGS)) \
	$(addprefix -LDFLAGS , $(LDFLAGS))

run:bin
	$(call git_commit, "npc simulation")
	$(BIN) $(NPC_FLAGS)


clean:
	rm -rf $(OBJ_DIR)

.PHONY:clean
include ../../Makefile
