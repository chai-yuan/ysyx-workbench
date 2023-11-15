package core.EXE

import chisel3._
import chisel3.util._
import core.WB._


class EXEMemLoadBundle extends Bundle {
  val loadEn   = Output(Bool())
  val loadAddr = Output(UInt(5.W))
}

class EXE2GlobalBundle extends Bundle {
  val forward    = new WriteBackBundle
  val exeMemLoad = new EXEMemLoadBundle
}
