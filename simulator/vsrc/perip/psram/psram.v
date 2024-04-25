module psram(
  input sck,
  input ce_n,
  inout [3:0] dio
);
  reg [7:0] mem[(1024*1024*4)-1:0];

  reg [7:0] count;

  typedef enum [2:0] { CMD, RADDR, WADDR, RWAIT, RDATA, WDATA } state_t;
  reg [2:0] state;
  reg [7:0] cmd;
  reg [23:0] addr;
  reg [31:0] output_data;
  assign dio = (state == RDATA) ? output_data[31:28]:
               4'bzzzz;
  always @(posedge sck or posedge ce_n) begin
    if(ce_n) begin
      state <= CMD;
    end

    else begin
      case(state)
        CMD: begin
          cmd[count] <= dio[0];
          state <= (count == 8'h7 && cmd[6:0] == 7'b1010111) ? RADDR :
                   (count == 8'h7 && cmd[6:0] == 7'b0011100) ? WADDR :
                    state;
        end

        RADDR: begin
          addr <= {addr[19:0],dio[3:0]};
          state <= (count == 8'd5) ? RWAIT:
                   state;
        end

        WADDR: begin
          addr <= {addr[19:0],dio[3:0]};
          state <= (count == 8'd5) ? WDATA:
                   state;
        end
        
        RWAIT: begin
          state <= (count == 8'd6) ? RDATA:
                   state;
          output_data <= {mem[{addr[23:2],2'b00}],
                          mem[{addr[23:2],2'b01}],
                          mem[{addr[23:2],2'b10}],
                          mem[{addr[23:2],2'b11}]};
        end

        RDATA: begin
          output_data <= {output_data[27:0],4'h0};
          state <= (count == 8'd7) ? CMD:
                   state;
        end

        WDATA: begin
          mem[addr] <= {mem[addr][3:0],dio[3:0]};
          addr <= addr + count[0];
        end
        
        default: state <= CMD;

      endcase
    end
  end

  always @(posedge sck or posedge ce_n) begin
    if(ce_n)
      count <= 8'h0;
    else begin
      case(state)
        CMD: count <= (count < 8'd7) ? count + 8'd1 : 8'd0;
        RADDR: count <= (count < 8'd5) ? count + 8'd1 : 8'd0;
        WADDR: count <= (count < 8'd5) ? count + 8'd1 : 8'd0;
        RWAIT: count <= (count < 8'd6) ? count + 8'd1 : 8'd0;
        RDATA: count <= (count < 8'd7) ? count + 8'd1 : 8'd0;
        WDATA: count <= (count < 8'd1) ? count + 8'd1 : 8'd0;
        default : count <= count + 8'd1;
      endcase
    end
  end  

endmodule
