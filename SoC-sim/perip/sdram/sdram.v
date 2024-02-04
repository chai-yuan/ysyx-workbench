module sdram(
  input        clk,
  input        cke,
  input        cs,
  input        ras,
  input        cas,
  input        we,
  input [12:0] a,
  input [ 1:0] ba,
  input [ 1:0] dqm,
  inout [15:0] dq
);

import "DPI-C" function void sdram_posedge(input int cmd, 
                                           input int a,
                                           input int ba,
                                           input int dqm,
                                           input int write_data,
                                           output int read_valid,
                                           output int read_data);
    wire [2:0]cmd;
    assign cmd = (~cs & ~ras & cas & we) ? 3'h1 :   // active
                 (~cs & ras & ~cas & we) ? 3'h2 :   // read
                 (~cs & ras & ~cas & ~we) ? 3'h3 :  // write
                 (~cs & ras & cas & ~we) ? 3'h4:    // terminate
                 (~cs & ~ras & ~cas & ~we) ? 3'h5 : // load_mode_register
                 3'h0;                              // nop

    wire sys_clk;
    assign sys_clk = cke ? clk : 1'b0;

    reg [31:0]read_valid;
    reg [31:0]read_data;
    assign dq = read_valid[0] ? read_data[15:0] : 16'hzzzz;

    always @(posedge sys_clk)begin
        sdram_posedge(cmd,a,ba,dqm,dq,read_valid,read_data);
    end


    // parameter data_bits = 16;
    // parameter mem_sizes = 8192*512;
    //
    // reg [data_bits - 1 : 0] Bank [0 : mem_sizes][0:3];
    //
    // reg [12:0] mode_reg;
    // reg [12:0] select_row_reg;
    // reg [8:0] select_column_reg;
    // reg [1:0] select_bank_reg;
    //
    // parameter state_nop = 2'd0;
    // parameter state_write = 2'd1;
    // parameter state_read = 2'd2;
    // reg [1:0] state;
    // reg read_valid;
    // reg [15:0] read_data;
    // reg [2:0] read_delay_cnt;
    // assign dq = read_valid ? read_data : 16'hzzzz;
    //
    // wire sys_clk;
    // assign sys_clk = cke ? clk : 1'b0;
    //
    // wire [2:0]burst_length;
    // wire [2:0]cas_latency;
    // assign burst_length = mode_reg[2:0];
    // assign cas_latency = mode_reg[6:4];
    //
    // wire cmd_nop;
    // wire cmd_active;
    // wire cmd_read;
    // wire cmd_write;
    // wire cmd_terminate;
    // wire cmd_load_mode_register;
    //
    // assign cmd_nop = ~cs & ras & cas & we;
    // assign cmd_active = ~cs & ~ras & cas & we;
    // assign cmd_read = ~cs & ras & ~cas & we;
    // assign cmd_write = ~cs & ras & ~cas & ~we;
    // assign cmd_terminate = ~cs & ras & cas & ~we;
    // assign cmd_load_mode_register = ~cs & ~ras & ~cas & ~we;
    //
    // always @(posedge sys_clk)begin
    //     if(cmd_load_mode_register)begin
    //         mode_reg <= a;
    //     end
    //
    //     if(cmd_active)begin
    //        select_row_reg <= a;
    //        select_bank_reg <= ba;
    //     end
    //
    //     if(cmd_write)begin
    //         state <= state_write;
    //         select_column_reg <= a[8:0];
    //         select_bank_reg <= ba;
    //         Bank[{select_row_reg,a[8:0]}][ba] <= dq;
    //     end
    //
    //     if(cmd_read)begin
    //         state <= state_read;
    //         select_column_reg <= a[8:0];
    //         select_bank_reg <= ba;
    //         read_delay_cnt <= cas_latency - 1;
    //     end
    //
    //     if(cmd_terminate)begin
    //         state <= state_nop;
    //     end
    //
    //     if(cmd_nop)begin //处理突发写入读取
    //     end
    // end

endmodule
