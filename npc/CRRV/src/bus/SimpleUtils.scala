package bus

import chisel3._
import chisel3.util._
import config.CPUconfig._
import io._
import sim.Debug

class SimpleMux2(val addrWidth: Int, val dataWidth: Int) extends Module {
  val io = IO(new Bundle {
    val sel2 = Input(Bool())
    val in   = Flipped(new SimpleIO(addrWidth, dataWidth))
    val out1 = new SimpleIO(addrWidth, dataWidth)
    val out2 = new SimpleIO(addrWidth, dataWidth)
  })
  val readSel = RegNext(io.sel2)

  io.in.in.rdata := Mux(readSel, io.out2.in.rdata, io.out1.in.rdata)

  io.in.out.ready := Mux(io.sel2, io.out2.out.ready, io.out1.out.ready)

  io.out1.out.valid := io.in.out.valid && !io.sel2
  io.out1.out.bits  := io.in.out.bits

  io.out2.out.valid := io.in.out.valid && io.sel2
  io.out2.out.bits  := io.in.out.bits
}
