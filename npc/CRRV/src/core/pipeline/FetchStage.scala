package core.pipeline

import chisel3._
import chisel3.util._
import io._
import config.CPUconfig._
import core.brachpediction.BranchPredictor

class FetchStage extends Module {
  val io = IO(new Bundle {
    val control = new FetchStageControlIO
    val instRom = Decoupled(new SimpleOutIO(ADDR_WIDTH, INST_WIDTH))
    val if2id   = Output(new IF2IDIO)

    val branchInfo = Input(new BranchInfoIO(5))
  })
  val pc  = RegInit(RESET_PC)
  val bpu = Module(new BranchPredictor)
  bpu.io.branchInfo <> io.branchInfo
  bpu.io.lookupPc := pc

  val nextPc = MuxCase(
    pc + 4.U,
    Seq(
      (io.control.flush) -> (io.control.flushPC),
      (io.control.stall) -> (pc),
      (bpu.io.predTaken) -> (bpu.io.predTarget)
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
  io.if2id.IF.instValid  := io.instRom.ready
  io.if2id.IF.pc         := pc
  io.if2id.IF.predTaken  := bpu.io.predTaken
  io.if2id.IF.predTarget := bpu.io.predTarget
  io.if2id.IF.predIndex  := bpu.io.predIndex
}

class FetchStageControlIO extends Bundle {
  val flush   = Input(Bool())
  val flushPC = Input(UInt(ADDR_WIDTH.W))
  val stall   = Input(Bool())

  val stallReq = Output(Bool())
}
