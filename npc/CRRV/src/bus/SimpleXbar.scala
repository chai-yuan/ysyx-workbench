package bus

import chisel3._
import chisel3.util._
import config.CPUconfig._
import io._

class SimpleXbar extends Module {
  val io = IO(new Bundle {
    val simpleIn    = Flipped(new SimpleIO(ADDR_WIDTH, DATA_WIDTH))
    val simpleOut   = new SimpleIO(ADDR_WIDTH, DATA_WIDTH)
    val simpleCLINT = new SimpleIO(ADDR_WIDTH, DATA_WIDTH)
  })

  val selCLINT = io.simpleIn.out.bits.addr(31, 16) === "h0200".U
  val mux2    = Module(new SimpleMux2(ADDR_WIDTH, DATA_WIDTH))

  mux2.io.sel2 := selCLINT
  mux2.io.in <> io.simpleIn
  mux2.io.out1 <> io.simpleOut
  mux2.io.out2 <> io.simpleCLINT
}
