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
    val if2id = new IF2IDBundle

    val pc   = Input(UInt(32.W))
    val inst = Input(UInt(32.W))

    val ifStop  = Input(Bool())
    val ifFlush = Input(Bool())
  })

  val regs = RegInit(0.U(64.W))

  regs := MuxCase(
    Cat(io.pc, io.inst),
    Seq(
      (io.ifFlush) -> (0.U),
      (io.ifStop) -> (regs)
    )
  )

  io.if2id.pc   := regs(0, 31)
  io.if2id.inst := regs(32, 63)
}
