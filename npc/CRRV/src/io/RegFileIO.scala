package io

import chisel3._
import chisel3.util._
import config.CPUconfig._

class RegReadIO extends Bundle {
  val en   = Output(Bool())
  val addr = Output(UInt(5.W))
  val data = Input(UInt(DATA_WIDTH.W))
}

class RegWriteIO extends Bundle {
  val en   = Output(Bool())
  val addr = Output(UInt(5.W))
  val data = Output(UInt(DATA_WIDTH.W))
}

class RegForwardIO extends Bundle {
  val en    = Bool()
  val addr  = UInt(5.W)
  val data  = UInt(DATA_WIDTH.W)
  val load  = Bool()            // 访问内存标志
}
