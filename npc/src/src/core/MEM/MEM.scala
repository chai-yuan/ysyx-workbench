package core.MEM

import chisel3._
import chisel3.util._
import core.EXE._
import core.MemBundle
import core.ID.ControlBundle

class MEM extends Module {
  val io = IO(new Bundle {
    val exe2mem = Flipped(Decoupled(new EXE2MEMBundle))
    val mem2wb  = Decoupled(new MEM2WBBundle)
    // global
    val globalmem = Flipped(new DataMemGlobalBundle)
  })

  // pipeline ctrl
  val readyGo    = true.B
  val memValid   = RegInit(false.B)
  val wbAllowin  = io.mem2wb.ready
  val memAllowin = !memValid || (readyGo && wbAllowin)
  memValid := Mux(memAllowin, io.exe2mem.valid, memValid)
  val wbValid = memValid && readyGo

  io.mem2wb.valid  := wbValid
  io.exe2mem.ready := memAllowin

  // from if data
  val exe2mem = RegInit(0.U.asTypeOf(new EXE2MEMBundle))
  exe2mem := Mux(io.exe2mem.valid && memAllowin, io.exe2mem.bits, exe2mem)
  val memData = io.globalmem.memData

  // to wb data
  val mem2wbData = Wire(new MEM2WBBundle)
  mem2wbData.ifdata          := exe2mem.ifdata
  mem2wbData.iddata          := exe2mem.iddata
  mem2wbData.exedata         := exe2mem.exedata
  mem2wbData.memdata.memData := memData

  io.mem2wb.bits := mem2wbData

  // mem2global
}
