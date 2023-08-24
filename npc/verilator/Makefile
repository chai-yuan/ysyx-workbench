VERILATOR = verilator
VFLAGS =  --cc
TARGET = CPUTop
CXXSRC = main.cpp
EXECUTABLE = obj_dir/VCPUTop

.PHONY: all run clean

all: run

run: $(TARGET).v
	$(VERILATOR) $(VFLAGS) $(TARGET).v --exe $(CXXSRC)
	make -C obj_dir -f V$(TARGET).mk
	./$(EXECUTABLE)

clean:
	rm -rf obj_dir
