package io

import chisel3._
import chisel3.util._
import config.CPUconfig

class DebugIO extends Bundle {
  val valid = Bool()
  val halt  = Bool()

  val deviceAccess = Bool()
  val deviceAddr   = UInt(CPUconfig.ADDR_WIDTH.W)

  val pc       = UInt(CPUconfig.ADDR_WIDTH.W)
  val regWen   = Bool()
  val regWaddr = UInt(5.W)
  val regWdata = UInt(CPUconfig.DATA_WIDTH.W)
}
