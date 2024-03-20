package core.brachpediction

import chisel3._
import config.CPUconfig._
import core.define.OperationDefine._
import core.define.CsrDefine._
import chisel3.util.Cat
import chisel3.util.log2Ceil
import core.define.InstructionDefine

class BtbLine(val tagPcWidth: Int) extends Bundle {
  val jump   = Bool()
  val pc     = UInt(tagPcWidth.W)
  val target = UInt((ADDR_WIDTH - 2).W)
}

class BTB(val BTBSize: Int) extends Module {
  val io = IO(new Bundle {
    // 更新用分支信息
    val branch = Input(Bool())
    val jump   = Input(Bool())
    val pc     = Input(UInt(ADDR_WIDTH.W))
    val target = Input(UInt(ADDR_WIDTH.W))
    // 查询信息
    val lookupPc     = Input(UInt(ADDR_WIDTH.W))
    val lookupBranch = Output(Bool())
    val lookupJump   = Output(Bool())
    val lookupTarget = Output(UInt(ADDR_WIDTH.W))
  })
  val btbWidth   = log2Ceil(BTBSize)
  val tagPcWidth = ADDR_WIDTH - btbWidth - 2;

  val valids = RegInit(VecInit(Seq.fill(BTBSize) { false.B }))
  val lines  = Mem(BTBSize, new BtbLine(tagPcWidth))

  val tagPc       = io.pc(ADDR_WIDTH - 1, btbWidth + 2)
  val index       = io.pc(btbWidth + 1, 2)
  val lookupPcSel = io.lookupPc(ADDR_WIDTH - 1, btbWidth + 2)
  val lookupIndex = io.lookupPc(btbWidth + 1, 2)
  val btbHit      = valids(lookupIndex) && lines(lookupIndex).pc === lookupPcSel

  // 更新缓存
  when(io.branch) {
    valids(index)       := true.B
    lines(index).jump   := io.jump
    lines(index).pc     := tagPc
    lines(index).target := io.target(ADDR_WIDTH - 1, 2)
  }

  // 查找缓存
  io.lookupBranch := btbHit
  io.lookupJump   := Mux(btbHit, lines(lookupIndex).jump, false.B)
  io.lookupTarget := Cat(Mux(btbHit, lines(lookupIndex).target, 0.U), 0.U(2.W))
}
