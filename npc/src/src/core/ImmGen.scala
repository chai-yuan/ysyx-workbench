package core

import chisel3._
import config.InstType._

class ImmGen extends Module {
  val io = IO(new Bundle {
    val instType = Input(UInt(3.W))
    val inst     = Input(UInt(32.W))
    val imm      = Output(UInt(32.W))
  })

  io.imm := Mux(io.instType === Inst_I, io.inst(31, 20), 0.U)
}
