package core.WB

import chisel3._
import chisel3.util._
import config.Config

class WB2IDBundle extends Bundle {
  val regwen   = Output(Bool())
  val regwdata = Output(UInt(32.W))
}
