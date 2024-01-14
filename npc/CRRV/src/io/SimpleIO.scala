package io

import chisel3._
import chisel3.util._

class SimpleOutIO(val addrWidth: Int, val dataWidth: Int) extends Bundle {
  val addr    = Output(UInt(addrWidth.W))
  val writeEn = Output(Bool()) // false为读
  val size    = Output(UInt(log2Ceil(dataWidth / 8).W))
  val wdata   = Output(UInt(dataWidth.W))
}

class SimpleInIO(val dataWidth: Int) extends Bundle {
  val rdata = Input(UInt(dataWidth.W))
  // val error = Input(Bool()) // 读写数据发生异常
}

/**
  * 处理器的内部总线，当out信号被成功处理的下一个周期，in信号有效
  *
  * @param addrWidth
  * @param dataWidth
  */
class SimpleIO(val addrWidth: Int, val dataWidth: Int) extends Bundle {
  val out = Decoupled(new SimpleOutIO(addrWidth, dataWidth))
  val in  = new SimpleInIO(dataWidth)
}
