package io

import chisel3._
import chisel3.util._
import config.CPUconfig

class DebugIO extends Bundle {
  val pc   = Output(UInt(CPUconfig.ADDR_WIDTH.W))
  val regs = Output(Vec(32, UInt(32.W)))
  val skip = Output(Bool())
}
