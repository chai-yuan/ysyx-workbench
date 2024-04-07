package io

import chisel3._
import chisel3.util._
import config.CPUconfig

class DebugIO extends Bundle {
  val debugInfo = new DebugInfoIO
  val regs      = new RegDebugIO
  val csr       = new CsrDebugIO
  val intr      = Bool()
}

class DebugInfoIO extends Bundle {
  val valid        = Bool()
  val halt         = Bool()
  val deviceAccess = Bool()
  val deviceAddr   = UInt(CPUconfig.ADDR_WIDTH.W)
  val pc           = UInt(CPUconfig.ADDR_WIDTH.W)
}

class RegDebugIO extends Bundle {
  val regs = Vec(32, UInt(CPUconfig.DATA_WIDTH.W))
}

class CsrDebugIO extends Bundle {
  val mstatus  = UInt(32.W)
  val mcause   = UInt(32.W)
  val mtvec    = UInt(32.W)
  val mepc     = UInt(32.W)
  val mscratch = UInt(32.W)
  val mie      = UInt(32.W)
  val mip      = UInt(32.W)
  val mtval    = UInt(32.W)
}
