package core.brachpediction

import chisel3._
import chisel3.util._
import config.CPUconfig._
import core.define.OperationDefine._
import core.define.CsrDefine._
import io.BranchInfoIO

class BranchPredictor(val GHRWidth: Int = 5, val BTBSize: Int = 32) extends Module {
  val io = IO(new Bundle {
    val branchInfo = Input(new BranchInfoIO(GHRWidth))

    val lookupPc   = Input(UInt(ADDR_WIDTH.W))
    val predTaken  = Output(Bool())
    val predTarget = Output(UInt(ADDR_WIDTH.W))
    val predIndex  = Output(UInt(GHRWidth.W))
  })
  val ghr = Module(new GHR(GHRWidth))
  val pht = Module(new PHT(GHRWidth))
  val btb = Module(new BTB(BTBSize))
  // wire GHR
  ghr.io.branch := io.branchInfo.branch
  ghr.io.taken  := io.branchInfo.taken
  // wire PHT
  val index = io.lookupPc(GHRWidth + 1, 2) ^ ghr.io.ghr // G-share
  pht.io.lastBranch := io.branchInfo.branch
  pht.io.lastTaken  := io.branchInfo.taken
  pht.io.lastIndex  := io.branchInfo.index
  pht.io.index      := index
  // wire BTB
  btb.io.branch   := io.branchInfo.branch
  btb.io.jump     := io.branchInfo.jump
  btb.io.pc       := io.branchInfo.pc
  btb.io.target   := io.branchInfo.target
  btb.io.lookupPc := io.lookupPc
  // wire output signals
  io.predTaken  := btb.io.lookupBranch && (pht.io.taken || btb.io.lookupJump)
  io.predTarget := btb.io.lookupTarget
  io.predIndex  := index
}
