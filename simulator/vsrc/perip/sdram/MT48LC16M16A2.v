module MT48LC16M16A2(
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
    wire [2:0]cmd;
    assign cmd = (~cs & ~ras & cas & we) ? 3'h1 :   // active
                 (~cs & ras & ~cas & we) ? 3'h2 :   // read
                 (~cs & ras & ~cas & ~we) ? 3'h3 :  // write
                 (~cs & ras & cas & ~we) ? 3'h4:    // terminate
                 (~cs & ~ras & ~cas & ~we) ? 3'h5 : // load_mode_register

                 (~cs & ~ras & cas & ~we) ? 3'h6:    // PRECHARGE
                 (~cs & ~ras & ~cas & we) ? 3'h7:    // AUTO REFRESH
                 3'h0;                              // nop

    wire sys_clk;
    assign sys_clk = cke ? clk : 1'b0;

    reg [3:0] read_valid;
    reg [15:0] read_data [3:0];
    reg [1:0] read_idx;
    assign dq = read_valid[read_idx] ? read_data[read_idx][15:0] : 16'hzzzz;

    reg [15:0] sdram_mem [8192*512*4];
    reg [2:0] burst_length;
    reg [2:0] cas_latency;
    reg [12:0] active_row [4];

    reg read_burst;
    reg write_burst;
    reg [31:0] addr;
    wire [31:0] now_addr;
    assign now_addr = {active_row[ba][12:0],ba[1:0],a[8:0]};
    reg [7:0] burst_cnt;

    // 处理命令
    always @(posedge sys_clk) begin
      case(cmd)
        3'h1 : begin  // active
          active_row[ba] <= a[12:0];
        end

        3'h2 : begin  // read
          addr <= now_addr;
          //$display("read cmd!");
          burst_cnt   <= 8'h00;
          read_burst <= 1'b1;
        end

        3'h3 : begin  // write
          addr <= now_addr + 1;
          sdram_mem[now_addr][7:0] <= dqm[0] ? sdram_mem[now_addr][7:0] : dq[7:0];
          sdram_mem[now_addr][15:8] <= dqm[1] ? sdram_mem[now_addr][15:8] : dq[15:8];
          //$display("write cmd!");
          //$display("write dqm %x : %x -> %x",dqm,dq[15:0],now_addr);
          burst_cnt   <= 8'h01;
          write_burst <= 1'b1;
        end

        3'h4 : begin  // terminate
          write_burst <= 1'b0;
          read_burst  <= 1'b0;
          burst_cnt   <= 8'h00;
        end

        3'h5 : begin  // load_mode_register
          burst_length[2:0] <= (3'h1 << a[2:0]);
          cas_latency[2:0] <= a[6:4] - 3'b001;
        end

        default : begin // 处理突发传输
          if(burst_cnt < burst_length) begin
            if(read_burst) begin
              read_valid[read_idx[1:0] + cas_latency[1:0]] <= 1'b1;
              read_data[read_idx[1:0] + cas_latency[1:0]][15:0] <= sdram_mem[addr][15:0];
              //$display("read : %x -> %x",addr,sdram_mem[addr][15:0]);
            end

            if(write_burst) begin
              sdram_mem[addr][7:0] <= dqm[0] ? sdram_mem[addr][7:0] : dq[7:0];
              sdram_mem[addr][15:8] <= dqm[1] ? sdram_mem[addr][15:8] : dq[15:8];
              //$display("write dqm %x : %x -> %x",dqm,dq[15:0],addr);
            end

            burst_cnt <= burst_cnt + 8'h1;
            addr <= addr + 32'h1;
          end

          else begin
            write_burst <= 1'b0;
            read_burst  <= 1'b0;
          end

        end

      endcase
    end

    // 输出缓冲
    always @(posedge sys_clk) begin
      read_valid[read_idx[1:0]] <= 1'b0;
      read_idx[1:0] <= read_idx[1:0] + 2'b01;
    end

endmodule
