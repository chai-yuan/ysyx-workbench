package core.IF

import chisel3._
import chisel3.util._
import config.Config
import core.MemBundle

class IF extends Module {
  val io = IO(new Bundle {
    val preif2if = Flipped(Decoupled(new PreIF2IFBundle))
    val if2id = Decoupled(new IF2IDBundle)
    val if2global = new IF2GlobalBundle
  })

  // pipeline ctrl
  val readyGo = true.B
  val ifValid = RegInit(false.B)
  ifValid := Mux(ifAllowin, io.preif2if.valid, ifValid)
  val ifAllowin = !ifValid || (readyGo && idAllowin)
  val idValid = ifValid && readyGo
  val idAllowin = io.if2id.ready

  io.if2id.valid := idValid
  io.preif2if.ready := ifAllowin

  // from preif data
  val preif2if = io.preif2if.bits

  val pc = RegInit(Config.PCinit)
  pc := Mux(ifValid && ifAllowin, preif2if.nextPC, pc)
  val inst = preif2if.instData

  // to id data
  val if2id = Wire(new IF2IDBundle)
  if2id.ifdata.pc := pc
  if2id.ifdata.inst := preif2if.instData

  io.if2id.bits <> if2id

  // if2global
  io.if2global.pc := pc
}
