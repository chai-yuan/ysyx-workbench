package core.IF

import chisel3._
import chisel3.util._
import config.Config

class IFDataBundle extends Bundle {
  val pc = Output(UInt(32.W))
  val inst = Output(UInt(32.W))
}

class IF2IDBundle extends Bundle {
  val ifdata = new IFDataBundle
}
