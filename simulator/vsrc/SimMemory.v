import "DPI-C" function void sim_mem_read(input int addr, output int data);
import "DPI-C" function void sim_mem_write(input int addr, input int size, input int data);

module SimMemory(
    input wire clock,
    input wire reset,
    input wire simple_out_valid,
    output wire simple_out_ready,

    input wire [31:0] simple_out_bits_addr,
    input wire simple_out_bits_writeEn,
    input wire [2:0] simple_out_bits_size,
    input wire [31:0] simple_out_bits_wdata,
    output reg [31:0] simple_in_rdata
);

assign simple_out_ready = ~reset && simple_out_valid;

always @(posedge clock) begin
    if (~reset && simple_out_valid) begin
        if(simple_out_bits_writeEn) begin
            sim_mem_write(simple_out_bits_addr,simple_out_bits_size,simple_out_bits_wdata);
        end
        sim_mem_read(simple_out_bits_addr,simple_in_rdata);
    end
end

endmodule
