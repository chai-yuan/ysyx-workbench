## 定义Verilator命令行选项
VERILATOR_FLAGS := --cc --exe --build -j 0 -Wall

# 定义源文件路径
BUILD_DIR := $(shell pwd)/build
VSRC := vsrc/example.v
CSRC := csrc/main.cpp

# 定义目标可执行文件名
TARGET := Vexample
TARGET_PATH := $(BUILD_DIR)/$(TARGET)

# 默认规则，指定 make 时的执行动作
all: $(TARGET_PATH)

# Verilator 生成C++代码并编译成可执行文件
$(TARGET_PATH): $(VSRC) $(CSRC)
	mkdir -p $(BUILD_DIR)
	verilator $(VERILATOR_FLAGS) -Mdir $(BUILD_DIR) $(CSRC) $(VSRC) -o $(TARGET)

# 清理生成的文件
clean:
	rm -rf $(BUILD_DIR)

# 运行可执行文件
run: $(TARGET_PATH)
	$(TARGET_PATH)

.PHONY: all clean run