package core.WB

import chisel3._
import chisel3.util._

class WBDebugBundle extends Bundle {
  val valid = Output(Bool())
  val pc    = Output(UInt(32.W))
  val inst  = Output(UInt(32.W))
  val halt  = Output(Bool())
}

class WriteBackBundle extends Bundle {
  val enable = Output(Bool())
  val wAddr  = Output(UInt(5.W))
  val wData  = Output(UInt(32.W))
}

class WB2GlobalBundle extends Bundle {
  val debug     = new WBDebugBundle
  val writeBack = new WriteBackBundle
}
