package core.IF

import chisel3._
import chisel3.util._
import config.Config
import core.MemBundle
import core.Hazerd2IFBundle

class IFBundle extends Bundle {
  val instMem   = new MemBundle
  val hazerd2IF = Flipped(new Hazerd2IFBundle)
  val if2id     = new IF2IDBundle
}

class IF extends Module {
  val io    = IO(new IFBundle)
  val if2id = new IF2ID

  val pc = RegInit(Config.PCinit)

  val nextPC = MuxCase(
    pc,
    Seq(
      (pc === Config.PCinit) -> (pc + 4.U),
      (io.hazerd2IF.nextPCSel) -> (io.hazerd2IF.nextPC),
      (!io.hazerd2IF.nextPCSel) -> (pc + 4.U)
    )
  )
  pc := nextPC

  // inst mem
  io.instMem.addr      := nextPC
  io.instMem.readEn    := true.B
  io.instMem.writeEn   := false.B
  io.instMem.writeData := 0.U
  // if2id
  if2id.io.pc      := pc
  if2id.io.inst    := io.instMem.readData
  if2id.io.ifStop  := io.hazerd2IF.ifStop
  if2id.io.ifFlush := io.hazerd2IF.ifFlush
}
