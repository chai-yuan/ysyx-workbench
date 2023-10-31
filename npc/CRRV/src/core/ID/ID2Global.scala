package core.ID

import chisel3._
import chisel3.util._

class BranchBundle extends Bundle {
  val branchSel = Output(Bool())
  val branchTarget = Output(UInt(32.W))
}

class ID2GlobalBundle extends Bundle {
  val branch = new BranchBundle
  val debugRegs = Output(Vec(32, UInt(32.W)))
}
