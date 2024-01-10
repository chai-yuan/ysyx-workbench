VSRCS_DIR 	:= $(SIM_HOME)/soc
DIVIP_DIR	:= $(SIM_HOME)/perip

CSRCS_DIR 	:= $(SIM_HOME)/src
INCLUDE_DIR 	:= $(SIM_HOME)/include
OBJ_DIR 	:= $(SIM_HOME)/obj_dir

VSRCS += $(shell find $(abspath $(VSRCS_DIR)) -name "*.v")
VSRCS += $(shell find $(abspath $(DIVIP_DIR)) -name "*.v")

CSRCS += $(shell find $(abspath $(CSRCS_DIR)) -name "*.cpp" -or -name "*.c") 
HSRCS += $(shell find $(abspath $(INCLUDE_DIR)) -name "*.h")
HSRCS += $(shell find $(abspath $(OBJ_DIR)) -name "*.h")

# verilator
TOP_NAME := ysyxSoCFull

VERILATOR_FLAGS := -I$(SIM_HOME)/perip/uart16550/rtl -I$(SIM_HOME)/perip/spi/rtl
VERILATOR_FLAGS += --top $(TOP_NAME) -Wno-lint -Wno-style -Wno-UNUSED
VERILATOR_FLAGS += --trace --debug --cc --exe --build --no-timing --timescale "1ns/1ns"
BIN 		:= $(OBJ_DIR)/V$(TOP_NAME)

# gcc
CFLAGS 	:= -I$(INCLUDE_DIR) -g -Wall -Wextra 
# CFLAGS	+= -DVL_PRINTF=printf
# CFLAGS += -fPIE $(LLVM_CXXFLAGS)
LDFLAGS := -ldl -lreadline
# LDFLAGS += $(LLVM_LIBS)

IMG 	?=
ARGS 	?=

NPC_FLAGS += $(ARGS) $(IMG) 

bin:$(CSRCS) $(VSRCS)
	verilator $(VERILATOR_FLAGS) $(CSRCS) $(VSRCS) \
	$(addprefix -CFLAGS , $(CFLAGS)) \
	$(addprefix -LDFLAGS , $(LDFLAGS))

run:bin
	$(call git_commit, "npc simulation")
	@echo $(NPC_FLAGS) 
	$(BIN) $(NPC_FLAGS)


clean:
	rm -rf $(OBJ_DIR)

.PHONY:clean
include ../Makefile
