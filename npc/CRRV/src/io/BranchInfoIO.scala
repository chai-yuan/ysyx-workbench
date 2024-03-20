package io

import chisel3._
import chisel3.util._
import config.CPUconfig._
import core.define.OperationDefine._
import core.define.CsrDefine._

class BranchInfoIO(val GHTWidth: Int) extends Bundle {
  val branch = Bool() // last inst is a b/j
  val jump   = Bool() // is 'jal' or 'jalr'
  val taken  = Bool() // is last branch taken
  val index  = UInt(GHTWidth.W) // last index of PHT
  val pc     = UInt(ADDR_WIDTH.W) // last instruction PC
  val target = UInt(ADDR_WIDTH.W) // last branch target
}
