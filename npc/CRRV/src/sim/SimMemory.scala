package sim

import chisel3._
import chisel3.util._
import config.CPUconfig._
import io.SimpleIO

class SimMemory extends BlackBox {
  val io = IO(new Bundle {
    val clock  = Input(Clock())
    val reset  = Input(Reset())
    val simple = Flipped(new SimpleIO(ADDR_WIDTH, DATA_WIDTH))
  })
}
