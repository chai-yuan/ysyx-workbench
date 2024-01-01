package sim

import chisel3._
import chisel3.util._
import io.DebugIO

class Debug extends BlackBox {
  val io = IO(new Bundle {
    val clock = Input(Clock())
    val reset = Input(Reset())
    val debug = Input(new DebugIO)
  })
}
