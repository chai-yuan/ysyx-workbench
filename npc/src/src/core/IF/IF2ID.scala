package core.IF

import chisel3._
import chisel3.util._
import config.Config

class IF2IDBundle extends Bundle {
  val pc   = Output(UInt(32.W))
  val inst = Output(UInt(32.W))
}

class IF2ID extends Module {
  val io = IO(new Bundle {
    val if2id   = new IF2IDBundle
    val ifIn    = Flipped(new IF2IDBundle)
    val ifFlush = Input(Bool())
  })

  val zeroWire = Wire(new IF2IDBundle)
  zeroWire := 0.U.asTypeOf(new IF2IDBundle)

  val regs = RegNext(Mux(io.ifFlush, zeroWire, io.ifIn))

  io.if2id := regs
}
