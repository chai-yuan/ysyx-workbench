module SimMemory(
    input wire clock,
    input wire reset,
    input wire simple_out_valid,
    input wire [31:0] simple_out_bits_addr,
    input wire simple_out_bits_writeEn,
    input wire [2:0] simple_out_bits_size,
    input wire [31:0] simple_out_bits_wdata,
    output wire simple_out_ready,
    output reg [31:0] simple_in_rdata
);

import "DPI-C" function void sim_mem_read(int addr, int *data);
import "DPI-C" function void sim_mem_write(int addr, int size, int data);

reg [31:0] read_data;

assign simple_out_ready = 1'b1;

always @(posedge clock) begin
    simple_in_rdata <= read_data;
    if (~reset && simple_out_valid) begin
        if(simple_out_bits_writeEn) begin
            sim_mem_write(simple_out_bits_addr,simple_out_bits_size,simple_out_bits_wdata);
        end
        sim_mem_read(simple_out_bits_addr,&read_data);
    end
end

endmodule
