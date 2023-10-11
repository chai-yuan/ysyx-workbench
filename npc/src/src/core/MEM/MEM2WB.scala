package core.MEM

import chisel3._
import chisel3.util._
import core.ID.ControlBundle

class MEM2WBBundle extends Bundle {
  val control = new ControlBundle

  val inst      = Output(UInt(32.W))
  val aluResult = Output(UInt(32.W))
  val memResult = Output(UInt(32.W))
}

class MEM2WB extends Module {
  val io = IO(new Bundle {
    val mem2wb = new MEM2WBBundle
    val memIn  = Flipped(new MEM2WBBundle)

    val memFlush = Input(Bool())
  })
  val zeroWire = Wire(new MEM2WBBundle)
  zeroWire := 0.U.asTypeOf(new MEM2WBBundle)

  val regs = RegNext(Mux(io.memFlush, zeroWire, io.memIn))

  io.mem2wb := regs
}
