package core.pipeline

import chisel3._
import chisel3.util._
import io._
import config.CPUconfig._

class FetchStage extends Module {
  val io = IO(new Bundle {
    val control = new FetchStageControlIO
    val instRom = new SimpleMemIO(ADDR_WIDTH, INST_WIDTH)
    val if2id   = Output(new FetchStageIO)
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
  io.control.stallReq := !io.instRom.valid
  // inst rom
  io.instRom.enable := true.B
  io.instRom.addr   := pc
  io.instRom.wen    := 0.U
  io.instRom.wdata  := 0.U
  // to id
  io.if2id.instValid := io.instRom.valid
  io.if2id.pc        := pc
}

class FetchStageControlIO extends Bundle {
  val flush   = Input(Bool())
  val flushPC = Input(UInt(ADDR_WIDTH.W))
  val stall   = Input(Bool())

  val stallReq = Output(Bool())
}
