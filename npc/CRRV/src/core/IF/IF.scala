package core.IF

import chisel3._
import chisel3.util._
import config.Config
import core.ID._

class IF extends Module {
  val io = IO(new Bundle {
    val preif2if  = Flipped(Decoupled(new PreIF2IFBundle))
    val if2id     = Decoupled(new IF2IDBundle)
    val if2global = new IF2GlobalBundle
    val branch    = Flipped(new BranchBundle)
  })

  // pipeline ctrl
  val readyGo   = true.B
  val ifValid   = RegInit(false.B)
  val idAllowin = io.if2id.ready
  val ifAllowin = !ifValid || (readyGo && idAllowin)
  ifValid := Mux(ifAllowin, io.preif2if.valid, ifValid)
  val idValid = ifValid && readyGo && !io.branch.branchSel

  io.if2id.valid    := idValid
  io.preif2if.ready := ifAllowin

  // from preif data
  val preif2if = io.preif2if.bits

  val pc = RegInit(Config.PCinit)
  pc := Mux(io.preif2if.valid && ifAllowin, preif2if.nextPC, pc)
  val inst = preif2if.instData

  // to id data
  val if2id = Wire(new IF2IDBundle)
  if2id.ifdata.pc   := pc
  if2id.ifdata.inst := inst

  io.if2id.bits := if2id

  // if2global
  io.if2global.pc := pc
}
