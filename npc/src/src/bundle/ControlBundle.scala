package bundle

import chisel3._

class ControlBundle extends Bundle {
  val regWriteEnable = Output(Bool())
  val ALUop          = Output(UInt(4.W))
  val ALUsrc2imm     = Output(Bool())
  val ALUunsigned    = Output(Bool())
  val branch         = Output(Bool())
  val mem2reg        = Output(Bool())
  val memWriteEnable = Output(Bool())
  val memWe          = Output(UInt(4.W))

  val jal   = Output(Bool())
  val jalr  = Output(Bool())
  val lui   = Output(Bool())
  val auipc = Output(Bool())
}
