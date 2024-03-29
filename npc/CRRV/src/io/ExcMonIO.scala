package io

import chisel3._
import config.CPUconfig._

class ExcMonCheckIO extends Bundle {
  val addr  = Output(UInt(ADDR_WIDTH.W))
  val valid = Input(Bool())
}

class ExcMonCommitIO extends Bundle {
  val addr  = UInt(ADDR_WIDTH.W)
  val set   = Bool()
  val clear = Bool()
}
