package sim

import chisel3._
import chisel3.util._

class DPIC_serial extends BlackBox with HasBlackBoxInline {
  val io = IO(new Bundle {
    val clock = Input(Clock())
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
    "DPIC_serial.sv",
    """
      |module DPIC_serial (
      |    input clock,
      |    input reset,
      |    input ren,
      |    input [31:0] raddr,
      |    output [31:0] rdata,
      |    input wen,
      |    input [31:0] waddr,
      |    input [31:0] wdata,
      |    input [3:0] wmask
      |);
      |    import "DPI-C" function void verilog_serial_read(input int raddr, output int rdata);
      |    import "DPI-C" function void verilog_serial_write(input int waddr, input int wdata, input byte wmask);
      |    always @(posedge clock) begin
      |        if (wen && !reset) begin
      |            verilog_serial_write(waddr, wdata, wmask);
      |        end
      |        if (ren && !reset) begin
      |            verilog_serial_read(raddr, rdata);
      |        end
      |    end
      |endmodule
  """.stripMargin
  )

}