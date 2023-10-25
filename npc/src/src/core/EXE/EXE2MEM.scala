package core.EXE

import chisel3._
import chisel3.util._
import core.ID._
import core.IF.IF2IDBundle
import core.IF.IFDataBundle

class EXEDataBundle extends Bundle {
  val aluResult = Output(UInt(32.W))
}

class EXE2MEMBundle extends Bundle {
  val ifdata = new IFDataBundle
  val iddata = new IDDataBundle
  val exedata = new EXEDataBundle
}
