package core.brachpediction

import chisel3._
import chisel3.util.Cat

class GHR(val GHRWidth : Int) extends Module {
  val io = IO(new Bundle {
    val branch  = Input(Bool())
    val taken   = Input(Bool())

    val ghr     = Output(UInt(GHRWidth.W))
  })
  val ghr = Reg(UInt(GHRWidth.W))

  when (io.branch) {
    ghr := Cat(ghr(GHRWidth - 2, 0), io.taken)
  }

  io.ghr := ghr
}