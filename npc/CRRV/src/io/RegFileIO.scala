package io

import chisel3._
import chisel3.util._
import config.CPUconfig._

class RegReadIO extends Bundle{
    val addr = Output(UInt(5.W))
    val data = Input(UInt(DATA_WIDTH.W))
}

class RegWriteIO extends Bundle {
  val en    = Output(Bool())
  val addr  = Output(UInt(5.W))
  val data  = Output(UInt(DATA_WIDTH.W))
}