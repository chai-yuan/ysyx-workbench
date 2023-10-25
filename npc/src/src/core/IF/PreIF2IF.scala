package core.IF

import chisel3._
import chisel3.util._

class PreIF2IFBundle extends Bundle {
  val nextPC = Output(UInt(32.W))
  val instData = Output(UInt(32.W))
}
