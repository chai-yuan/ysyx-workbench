package io

import chisel3._
import chisel3.util._

class SimpleMemIO(val addrWidth: Int, val dataWidth: Int) extends Bundle {
  val enable = Output(Bool())

  val wen   = Output(UInt((dataWidth / 8).W))
  val addr  = Output(UInt(addrWidth.W))
  val wdata = Output(UInt(dataWidth.W))

  val valid = Input(Bool())
  val rdata = Input(UInt(dataWidth.W))
//   val fault = Input(Bool())
}
