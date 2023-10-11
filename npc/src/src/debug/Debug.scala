package debug

import chisel3._
import chisel3.util._

class DebugBundle extends Bundle {
  val pc   = Output(UInt(32.W))
  val regs = Output(Vec(32, UInt(32.W)))

  val halt = Output(Bool())
}
