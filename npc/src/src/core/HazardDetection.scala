package core

import chisel3._
import chisel3.util._
import config.Config

class Hazerd2IFBundle extends Bundle {
  val ifStop  = Output(Bool())
  val ifFlush = Output(Bool())

  val nextPCSel = Output(Bool())
  val nextPC    = Output(UInt(32.W))
}
