package core.IF

import chisel3._
import chisel3.util._
import core.ID.BranchBundle
import memory.SRAMBundle

class PreIF extends Module {
  val io = IO(new Bundle {
    val instMem  = Flipped(new SRAMBundle)
    val preif2if = Decoupled(new PreIF2IFBundle)
    val pc       = Input(UInt(32.W))
    val branch   = Flipped(new BranchBundle)
  })

  // pipeline ctrl
  val readyGo   = true.B
  val ifValid   = readyGo
  val ifAllowin = io.preif2if.ready
  io.preif2if.valid := ifValid

  // ---
  val pc = io.pc
  val nextPC = MuxCase(
    pc + 4.U,
    Seq(
      (io.branch.branchSel) -> (io.branch.branchTarget)
    )
  )

  // to if data
  val preif2if = Wire(new PreIF2IFBundle)
  preif2if.instData := io.instMem.rdata
  preif2if.nextPC   := nextPC

  io.preif2if.bits <> preif2if
  // instmem
  io.instMem.wen   := false.B
  io.instMem.waddr := 0.U
  io.instMem.wdata := 0.U
  io.instMem.ren   := ifAllowin
  io.instMem.raddr := nextPC
  io.instMem.wmask := "b1111".U
}
