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

  val regs = RegNext(Mux(io.ifFlush, 0.U, io.ifIn))

  io.if2id := regs
}
