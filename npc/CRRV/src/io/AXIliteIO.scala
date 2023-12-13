package io

import chisel3._
import chisel3.util._


class AXIliteAddrBundle(val addrWidth: Int) extends Bundle {
  val valid = Output(Bool())
  val ready = Input(Bool())
  val addr  = Output(UInt(addrWidth.W))
}

class AXIliteReadBundle(val dataWidth: Int) extends Bundle {
  val valid = Input(Bool())
  val ready = Output(Bool())
  val data  = Input(UInt(dataWidth.W))
  val resp  = Input(UInt(2.W))
}

class AXIliteWriteBundle(val dataWidth: Int) extends Bundle {
  val valid = Output(Bool())
  val ready = Input(Bool())
  val data  = Output(UInt(dataWidth.W))
  val strb  = Output(UInt((dataWidth / 8).W))
}

class AXIliteWriteBackBundle extends Bundle {
  val valid = Input(Bool())
  val ready = Output(Bool())
  val resp  = Input(UInt(2.W))
}

class AXIliteMasterBundle(val addrWidth: Int, val dataWidth: Int) extends Bundle {
  val ar = new AXIliteAddrBundle(addrWidth)
  val r  = new AXIliteReadBundle(dataWidth)
  val aw = new AXIliteAddrBundle(addrWidth)
  val w  = new AXIliteWriteBundle(dataWidth)
  val b  = new AXIliteWriteBackBundle
}
