BUILD_DIR = ./build

export PATH := $(PATH):$(abspath ./utils)

test:
	mill -i __.test

verilog:
	$(call git_commit, "generate verilog")
	mkdir -p $(BUILD_DIR)
	mkdir -p ./simulation/vsrc/
	mill -i __.test.runMain Elaborate -td $(BUILD_DIR)
	cp $(BUILD_DIR)/CRRVTop.v ../SoC-sim/soc/

help:
	mill -i __.test.runMain Elaborate --help

compile:
	mill -i __.compile

bsp:
	mill -i mill.bsp.BSP/install

reformat:
	mill -i __.reformat

checkformat:
	mill -i __.checkFormat

clean:
	-rm -rf $(BUILD_DIR)
	-rm -rf ./simulation/vsrc/

.PHONY: test verilog help compile bsp reformat checkformat clean

-include ../Makefile
