package core.pipeline

import chisel3._
import chisel3.util._
import io._
import config.CPUconfig._

class FetchStage extends Module {
  val io = IO(new Bundle {
    val control = new FetchStageControlIO
    val instRom = Decoupled(new SimpleOutIO(ADDR_WIDTH, INST_WIDTH))
    val if2id   = Output(new IF2IDIO)
  })

  val pc = RegInit(RESET_PC)

  val nextPc = MuxCase(
    pc + 4.U,
    Seq(
      (io.control.flush) -> (io.control.flushPC),
      (io.control.stall) -> (pc)
    )
  )
  pc := nextPc

  // control
  io.control.stallReq := !io.instRom.ready
  // inst rom
  io.instRom.valid        := true.B
  io.instRom.bits.addr    := pc
  io.instRom.bits.size    := 2.U
  io.instRom.bits.writeEn := false.B
  io.instRom.bits.wdata   := 0.U
  // to id
  io.if2id.IF.instValid := io.instRom.ready
  io.if2id.IF.pc        := pc
}

class FetchStageControlIO extends Bundle {
  val flush   = Input(Bool())
  val flushPC = Input(UInt(ADDR_WIDTH.W))
  val stall   = Input(Bool())

  val stallReq = Output(Bool())
}
