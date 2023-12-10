package io

import chisel3._
import chisel3.util._
import config.CPUconfig

class DebugIO extends Bundle {
  val pc       = Output(UInt(CPUconfig.ADDR_WIDTH.W))
  val regWen   = Output(Bool())
  val regWaddr = Output(UInt(5.W))
  val regWdata = Output(UInt(CPUconfig.DATA_WIDTH.W))
}
