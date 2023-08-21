package tools

import chisel3._

class ControlBundle extends Bundle {
  val instType       = Output(UInt(3.W))
  val regWriteEnable = Output(Bool())
  val ALUop          = Output(UInt(4.W))
  val ALUsrc2imm     = Output(Bool())
}
