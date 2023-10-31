package debug

import chisel3._
import chisel3.util._
import core.WB._

class DebugBundle extends Bundle {
  val regs = Output(Vec(32, UInt(32.W)))

  val wbDebug = new WBDebugBundle
}
