package io

import chisel3._
import chisel3.util._


class AXIliteAddrIO(val addrWidth: Int) extends Bundle {
  val valid = Output(Bool())
  val ready = Input(Bool())
  val addr  = Output(UInt(addrWidth.W))
}

class AXIliteReadIO(val dataWidth: Int) extends Bundle {
  val valid = Input(Bool())
  val ready = Output(Bool())
  val data  = Input(UInt(dataWidth.W))
  val resp  = Input(UInt(2.W))
}

class AXIliteWriteIO(val dataWidth: Int) extends Bundle {
  val valid = Output(Bool())
  val ready = Input(Bool())
  val data  = Output(UInt(dataWidth.W))
  val strb  = Output(UInt((dataWidth / 8).W))
}

class AXIliteWriteBackIO extends Bundle {
  val valid = Input(Bool())
  val ready = Output(Bool())
  val resp  = Input(UInt(2.W))
}

class AXIliteMasterIO(val addrWidth: Int, val dataWidth: Int) extends Bundle {
  val ar = new AXIliteAddrIO(addrWidth)
  val r  = new AXIliteReadIO(dataWidth)
  val aw = new AXIliteAddrIO(addrWidth)
  val w  = new AXIliteWriteIO(dataWidth)
  val b  = new AXIliteWriteBackIO
}
