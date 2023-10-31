package memory

import chisel3._
import chisel3.util._

class DPIC_RAM extends BlackBox with HasBlackBoxInline {
  val io = IO(new Bundle {
    val reset = Input(Bool())

    val ren   = Input(Bool())
    val raddr = Input(UInt(32.W))
    val rdata = Output(UInt(32.W))

    val wen   = Input(Bool())
    val waddr = Input(UInt(32.W))
    val wdata = Input(UInt(32.W))
    val wmask = Input(UInt(4.W))
  })

  setInline(
    "DPIC_RAM.sv",
    """
      |module DPIC_RAM (
      |    input reset,
      |    input ren,
      |    input [31:0] raddr,
      |    output [31:0] rdata,
      |    input wen,
      |    input [31:0] waddr,
      |    input [31:0] wdata,
      |    input [3:0] wmask
      |);
      |    import "DPI-C" function void verilog_pmem_read(input int raddr, output int rdata);
      |    import "DPI-C" function void verilog_pmem_write(input int waddr, input int wdata, input byte wmask);
      |    always @(*) begin
      |        if (wen && !reset) begin
      |            verilog_pmem_write(waddr, wdata, wmask);
      |        end
      |        if (ren && !reset) begin
      |            verilog_pmem_read(raddr, rdata);
      |        end
      |    end
      |endmodule
  """.stripMargin
  )

}
