package core.MEM

import chisel3._
import chisel3.util._
import core.ID.ControlBundle
import core.IF.IFDataBundle
import core.ID.IDDataBundle
import core.EXE.EXEDataBundle

class MEMDataBundle extends Bundle {
  val memData = Output(UInt(32.W))
}

class MEM2WBBundle extends Bundle {
  val ifdata = new IFDataBundle
  val iddata = new IDDataBundle
  val exedata = new EXEDataBundle
  val memdata = new MEMDataBundle
}
