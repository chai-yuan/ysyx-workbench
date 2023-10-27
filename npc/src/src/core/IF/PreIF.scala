package core.IF

import chisel3._
import chisel3.util._
import core.ID.BranchBundle
import core.MemBundle

class PreIF extends Module {
  val io = IO(new Bundle {
    val instMem  = new MemBundle
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
  preif2if.instData := io.instMem.readData
  preif2if.nextPC   := nextPC

  io.preif2if.bits <> preif2if
  // instmem
  io.instMem.writeEn   := false.B
  io.instMem.writeData := 0.U
  io.instMem.readEn    := ifAllowin
  io.instMem.addr      := nextPC
  io.instMem.mark      := "b1111".U
}
