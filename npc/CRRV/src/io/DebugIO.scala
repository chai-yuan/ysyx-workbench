package io

import chisel3._
import chisel3.util._

class DebugIO extends Bundle {
  val regs = Output(Vec(32, UInt(32.W)))
}
