package core.ID

import chisel3._
import chisel3.util._
import config.Config

class ID2EXEBundle extends Bundle {
  val control = new ControlBundle

  val reg1 = Output(UInt(32.W))
  val reg2 = Output(UInt(32.W))
  val imm  = Output(UInt(32.W))
  val inst = Output(UInt(32.W))
}

class ID2EXE extends Module {
  val io = IO(new Bundle {
    val id2exe  = new ID2EXEBundle
    val idIn    = Flipped(new ID2EXEBundle)
    val idFlush = Input(Bool())
  })

  val regs = RegNext(Mux(io.idFlush, 0.U, io.idIn))

  io.id2exe := regs
}
