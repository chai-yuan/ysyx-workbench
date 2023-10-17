package core.IF

import chisel3._
import chisel3.util._
import config.Config
import core.MemBundle
import core.Hazerd2IFBundle

class IFBundle extends Bundle {
  val instMem   = new MemBundle
  val hazerd2if = Flipped(new Hazerd2IFBundle)
  val if2id     = new IF2IDBundle
}

class IF extends Module {
  val io = IO(new IFBundle)

  val if2id = Module(new IF2ID)

  val pc = RegInit(Config.PCinit)

  val nextPC = MuxCase(
    pc + 4.U,
    Seq(
      (pc === Config.PCinit) -> (pc + 4.U),
      (io.hazerd2if.ifStop) -> (pc),
      (io.hazerd2if.nextPCSel) -> (io.hazerd2if.nextPC)
    )
  )
  pc := nextPC

  // inst mem
  io.instMem.addr      := pc
  io.instMem.readEn    := true.B
  io.instMem.writeEn   := false.B
  io.instMem.writeData := 0.U
  io.instMem.mark      := "b1111".U
  // if2id
  if2id.io.ifIn.pc   := pc
  if2id.io.ifIn.inst := io.instMem.readData
  if2id.io.ifFlush   := io.hazerd2if.ifFlush
  io.if2id           := if2id.io.if2id
}
