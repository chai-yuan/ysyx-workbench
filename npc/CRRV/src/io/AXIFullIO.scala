package io

import chisel3._
import chisel3.util._

class AXIAddrIO(val addrWidth: Int) extends Bundle {
  val valid = Output(Bool())
  val ready = Input(Bool())
  val addr  = Output(UInt(addrWidth.W))
  val id    = Output(UInt(4.W))
  val size  = Output(UInt(3.W))
  val len   = Output(UInt(8.W))
  val burst = Output(UInt(2.W))
}

class AXIReadIO(val dataWidth: Int) extends Bundle {
  val valid = Input(Bool())
  val ready = Output(Bool())
  val data  = Input(UInt(dataWidth.W))
  val id    = Input(UInt(4.W))
  val last  = Input(Bool())
  val resp  = Input(UInt(2.W))
}

class AXIWriteIO(val dataWidth: Int) extends Bundle {
  val valid = Output(Bool())
  val ready = Input(Bool())
  val data  = Output(UInt(dataWidth.W))
  val id    = Output(UInt(4.W))
  val last  = Output(Bool())
  val strb  = Output(UInt((dataWidth / 8).W))
}

class AXIWriteBackIO extends Bundle {
  val valid = Input(Bool())
  val ready = Output(Bool())
  val id    = Input(UInt(4.W))
  val resp  = Input(UInt(2.W))
}

/**
  * AXI总线
  * 包含了除ARPROT以外的全部必要信号 // TODO ARPROT感觉目前用不到
  * @param addrWidth
  * @param dataWidth
  */
class AXIMasterIO(val addrWidth: Int, val dataWidth: Int) extends Bundle {
  val ar = new AXIAddrIO(addrWidth)
  val r  = new AXIReadIO(dataWidth)
  val aw = new AXIAddrIO(addrWidth)
  val w  = new AXIWriteIO(dataWidth)
  val b  = new AXIWriteBackIO

  def setAXIDefaults(): Unit = {
    ar.valid := false.B
    ar.addr  := 0.U
    r.ready  := 0.U
    aw.valid := false.B
    aw.addr  := 0.U
    w.valid  := false.B
    w.data   := 0.U
    w.strb   := 0.U
    b.ready  := false.B
  }
}
