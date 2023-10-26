package core.EXE

import chisel3._
import chisel3.util._

class DataMemGlobalBundle extends Bundle {
  val memData = Output(UInt(32.W))
}

class EXE2GlobalBundle extends Bundle {
  val globalmem = new DataMemGlobalBundle
}