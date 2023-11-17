package core.IF

import chisel3._
import chisel3.util._
import config.Config
import core.ID._
import memory.ReadBundle

class IF extends Module {
  val io = IO(new Bundle {
    val instMem = new ReadBundle

    val preif2if  = Flipped(Decoupled(new PreIF2IFBundle))
    val if2id     = Decoupled(new IF2IDBundle)
    val if2global = new IF2GlobalBundle
    val branch    = Flipped(new BranchBundle)
  })
  val branchFlushReg = RegInit(false.B)
  val branchFlush    = io.branch.branchSel || branchFlushReg

  // pipeline ctrl
  val readyGo   = io.instMem.valid
  val ifValid   = RegInit(false.B)
  val idAllowin = io.if2id.ready
  val ifAllowin = !ifValid || (readyGo && idAllowin)
  ifValid := Mux(ifAllowin, io.preif2if.valid, ifValid)
  val idValid = ifValid && readyGo && !branchFlush

  io.if2id.valid    := idValid
  io.preif2if.ready := ifAllowin

  branchFlushReg := MuxCase(
    branchFlushReg,
    Seq(
      // 数据没有到齐，或者branch信号不能维持
      (!(readyGo && ifValid) && !branchFlushReg) -> (io.branch.branchSel),
      (readyGo && ifValid && idAllowin) -> (false.B)
    )
  )
  // from preif data
  val preif2if = io.preif2if.bits

  val pc = RegInit(Config.PCinit)
  pc := Mux(io.preif2if.valid && ifAllowin, preif2if.nextPC, pc)

  // instMem
  val inst = io.instMem.data
  io.instMem.ready := idAllowin

  // to id data
  val if2id = Wire(new IF2IDBundle)
  if2id.ifdata.pc   := pc
  if2id.ifdata.inst := inst

  io.if2id.bits := if2id

  // if2global
  io.if2global.pc := pc
}
