package io

import chisel3._
import chisel3.util._

class SimpleMemIO(val addrWidth: Int, val dataWidth: Int) extends Bundle {
  val enable = Output(Bool())
  val addr  = Output(UInt(addrWidth.W))

  val wen   = Output(UInt((dataWidth / 8).W))
  val wdata = Output(UInt(dataWidth.W))

  val valid = Input(Bool())             // 读数据有效 | 写数据完成
  val rdata = Input(UInt(dataWidth.W))
//   val fault = Input(Bool())          // 用于报告TLB缺页，暂不实现 
}
