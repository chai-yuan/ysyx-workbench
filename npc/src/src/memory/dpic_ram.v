module dpic_ram (
    input ren,
    input [31:0] raddr,
    output [31:0] rdata,
    input wen,
    input [31:0] waddr,
    input [31:0] wdata,
    input [3:0] wmask
);

    import "DPI-C" function void verilog_pmem_read(input int raddr, output int rdata);
    import "DPI-C" function void verilog_pmem_write(input int waddr, input int wdata, input byte wmask);

    always @(*) begin
        if (wen) begin
            verilog_pmem_write(waddr, wdata, wmask);
        end
        if (ren) begin
            verilog_pmem_read(raddr, rdata);
        end
    end
    
endmodule
