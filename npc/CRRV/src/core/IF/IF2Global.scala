package core.IF

import chisel3._
import chisel3.util._

class IF2GlobalBundle extends Bundle {
  val pc         = Output(UInt(32.W))
  //val rdataStall = Output(Bool())
}
