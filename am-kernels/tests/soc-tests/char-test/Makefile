CC	=riscv64-linux-gnu-gcc
CFLAGS	=-nostartfiles -fno-pic -mcmodel=medany -mstrict-align -static -march=rv32im_zicsr -mabi=ilp32e
OBJCOPY	=riscv64-linux-gnu-objcopy
OBJDUMP	=riscv64-linux-gnu-objdump

all: char-test.bin char-test.txt

char-test: char-test.o
	$(CC) $(CFLAGS) $< -o $@

char-test.o: char-test.c
	$(CC) $(CFLAGS) -c $< -o $@

char-test.bin: char-test
	$(OBJCOPY) -O binary -j .text $< $@

char-test.txt: char-test
	$(OBJDUMP) -S $< > $@

clean:
	rm -f char-test char-test.o char-test.bin char-test.txt
