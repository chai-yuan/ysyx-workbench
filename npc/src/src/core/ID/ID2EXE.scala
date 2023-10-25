package core.ID

import chisel3._
import chisel3.util._
import config.Config
import core.IF.IFDataBundle

class IDDataBundle extends Bundle {
  val control = new ControlBundle

  val reg1 = Output(UInt(32.W))
  val reg2 = Output(UInt(32.W))
  val imm = Output(UInt(32.W))
}

class ID2EXEBundle extends Bundle {
  val ifdata = new IFDataBundle
  val iddata = new IDDataBundle
}
