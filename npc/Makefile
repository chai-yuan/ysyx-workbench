# 定义源文件路径
BUILD_DIR := $(shell pwd)/build
VERILOG_DIR := $(shell pwd)/vsrc
CPP_DIR := $(shell pwd)/csrc

# 定义编译目标
TARGET := Vtop
TARGET_VCD := trace.vcd
VERILATOR_ARG := -Wall -trace -Mdir $(BUILD_DIR)

# 定义 Verilator 生成的仿真可执行文件
SIM_EXE := $(BUILD_DIR)/$(TARGET)

# 编译仿真结果
all: $(SIM_EXE)

$(SIM_EXE): $(VERILOG_DIR)/*.v $(CPP_DIR)/*.cpp
	mkdir -p $(BUILD_DIR)
	verilator $(VERILATOR_ARG) -cc $(VERILOG_DIR)/top.v --exe $(CPP_DIR)/main.cpp
	make -C $(BUILD_DIR) -f $(TARGET).mk

# 进行仿真，同时生成波形文件
sim: $(SIM_EXE)
	$(call git_commit, "sim RTL") # DO NOT REMOVE THIS LINE!!!
	$(SIM_EXE) $(BUILD_DIR)/$(TARGET_VCD)
	gtkwave $(BUILD_DIR)/$(TARGET_VCD) &

clean:
	rm -rf $(BUILD_DIR)/*

.PHONY: clean

include ../Makefile
