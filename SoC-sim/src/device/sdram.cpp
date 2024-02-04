#include <cstdio>
#include <queue>

static unsigned short sdram[8192 * 512 * 4];

static int burst_length, cas_latency;
static int active_row, active_col, active_bank;

static bool read_burst, write_burst;
static int addr, addr_idx;
static std::queue<unsigned short> read_buff;

static void active(int a, int ba) {
    active_row = a & 0x1fff;
    active_bank = ba;
}

static void read(int a, int ba) {
    active_col = a & 0x1ff;
    active_bank = ba;

    addr_idx = addr = (active_row << 11) + (active_bank << 9) + active_col;
    printf("读命令 : addr %x\n", addr);

    read_burst = true;
    if (read_buff.empty()) {  // 放入读出延迟
        for (int i = 0; i < cas_latency - 1; i++) {
            read_buff.push(0);
        }
    }
}

static void write(int a, int ba) {
    active_col = a & 0x1ff;
    active_bank = ba;

    addr_idx = addr = (active_row << 11) + (active_bank << 9) + active_col;
    printf("写命令 : addr %x\n", addr);
    write_burst = true;
}

static void terminate() {
    write_burst = false;
    read_burst = false;
}

static void set_mode(int a) {
    burst_length = (a & 0b111);
    burst_length = 1 << burst_length;

    cas_latency = (a >> 4) & 0b111;
    printf("设置寄存器: burst_length: %d,cas_latency: %d\n a : %d\n",
     burst_length, cas_latency, a);
}

extern "C" void sdram_posedge(int cmd,
                              int a,
                              int ba,
                              int dqm,
                              int write_data,
                              int* read_valid,
                              int* read_data) {
    switch (cmd) {
        case 1: {
            active(a, ba);
            break;
        }
        case 2: {
            read(a, ba);
            break;
        }
        case 3: {
            write(a, ba);
            break;
        }
        case 4: {
            terminate();
            break;
        }
        case 5: {
            set_mode(a);
            break;
        }
    }

    if (read_burst) {
        read_buff.push(sdram[addr_idx]);
        // printf("读出 : addr: %x,data: %x,dqm: %x\n", addr_idx,
        // sdram[addr_idx],
        //        dqm);
        addr_idx++;
        if ((addr_idx - addr) == burst_length)
            read_burst = false;
    } else if (write_burst) {
        // printf("写入 : addr: %x,data: %x,dqm: %x\n", addr_idx, write_data,
        // dqm);
        unsigned short write_ = sdram[addr_idx];
        if (dqm == 2) {
            write_ = (write_ & 0xff00) | (write_data & 0x00ff);
        } else if (dqm == 1) {
            write_ = (write_ & 0x00ff) | (write_data & 0xff00);
        } else if (dqm == 0) {
            write_ = write_data;
        }

        sdram[addr_idx] = write_;
        addr_idx++;
        if ((addr_idx - addr) == burst_length)
            write_burst = false;
    }

    if (!read_buff.empty()) {
        *read_valid = 1;
        *read_data = read_buff.front();
        read_buff.pop();
    }else{
      *read_valid = 0;
    }
}
