VSRCS_DIR 	:= $(SOC_HOME)/soc
DIVIP_DIR	:= $(SOC_HOME)/perip

CSRCS_DIR 	:= $(SOC_HOME)/src
INCLUDE_DIR 	:= $(SOC_HOME)/include
OBJ_DIR 	:= $(SOC_HOME)/obj_dir

VSRCS += $(shell find $(abspath $(VSRCS_DIR)) -name "*.v")
VSRCS += $(shell find $(abspath $(DIVIP_DIR)) -name "*.v")

CSRCS += $(shell find $(abspath $(CSRCS_DIR)) -name "*.cpp" -or -name "*.c") 

# rules for NVBoard
include $(NVBOARD_HOME)/scripts/nvboard.mk

# verilator
TOP_NAME := ysyxSoCFull

VERILATOR_FLAGS := -I$(SOC_HOME)/perip/uart16550/rtl -I$(SOC_HOME)/perip/spi/rtl
VERILATOR_FLAGS += --top $(TOP_NAME) -Wno-lint -Wno-style -Wno-UNUSED
VERILATOR_FLAGS += --trace --debug --cc --exe --build --no-timing --timescale "1ns/1ns"
BIN 		:= $(OBJ_DIR)/V$(TOP_NAME)

# gcc
CFLAGS 	:= -I$(INCLUDE_DIR) -I$(NVBOARD_HOME)/usr/include -g -Wall -Wextra
# CFLAGS += -fPIE $(LLVM_CXXFLAGS)
LDFLAGS := -ldl -lreadline
LDFLAGS += $(shell sdl2-config --libs) -lSDL2_image -lSDL2_ttf

IMG 	?=
ARGS 	?=

SOC_FLAGS += $(ARGS) $(IMG) 

bin:$(CSRCS) $(VSRCS) $(NVBOARD_ARCHIVE)
	verilator $(VERILATOR_FLAGS) $(CSRCS) $(VSRCS) $(NVBOARD_ARCHIVE) \
	$(addprefix -CFLAGS , $(CFLAGS)) \
	$(addprefix -LDFLAGS , $(LDFLAGS))

run:bin
	$(call git_commit, "soc simulation")
	@echo $(SOC_FLAGS) 
	$(BIN) $(SOC_FLAGS)


clean:
	rm -rf $(OBJ_DIR)

.PHONY:clean
include ../Makefile
