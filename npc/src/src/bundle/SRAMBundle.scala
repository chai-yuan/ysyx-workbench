package bundle

import chisel3._
import chisel3.util._

class SRAMBundle extends Bundle {
  val en    = Output(Bool())
  val we    = Output(UInt(4.W))
  val addr  = Output(UInt(32.W))
  val wdata = Output(UInt(32.W))
  val rdata = Input(UInt(32.W))
}