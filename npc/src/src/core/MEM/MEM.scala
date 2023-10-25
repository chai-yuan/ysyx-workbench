package core.MEM

import chisel3._
import chisel3.util._
import core.EXE._
import core.MemBundle
import core.ID.ControlBundle

class MEM extends Module {
  val io = IO(new Bundle {
    val exe2mem = Flipped(Decoupled(new EXE2MEMBundle))
    val mem2wb = Decoupled(new MEM2WBBundle)
    // global
    val globalmem = new DataMemGlobalBundle
  })

  // pipeline ctrl
  val readyGo = true.B
  val memValid = RegInit(false.B)
  memValid := Mux(memAllowin, io.exe2mem.valid, memValid)
  val memAllowin = !memValid || (readyGo && wbAllowin)
  val wbValid = memValid && readyGo
  val wbAllowin = io.mem2wb.ready

  io.mem2wb.valid := wbValid
  io.exe2mem.ready := memAllowin

  // from if data
  val exe2mem = RegInit(0.U.asTypeOf(new EXE2MEMBundle))
  exe2mem := Mux(memValid && memAllowin, io.exe2mem.bits, exe2mem)
  val memData = io.globalmem.memData

  // to wb data
  val mem2wbData = Wire(new MEM2WBBundle)
  mem2wbData.ifdata <> io.exe2mem.bits.ifdata
  mem2wbData.iddata <> io.exe2mem.bits.iddata
  mem2wbData.exedata <> io.exe2mem.bits.exedata
  mem2wbData.memdata.memData := memData
  // mem2global
}
