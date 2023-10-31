package core.MEM

import chisel3._
import chisel3.util._
import core.EXE._
import core.ID.ControlBundle
import config.WriteBackOp

class MEM extends Module {
  val io = IO(new Bundle {
    val exe2mem    = Flipped(Decoupled(new EXE2MEMBundle))
    val mem2wb     = Decoupled(new MEM2WBBundle)
    val mem2global = new MEM2GlobalBundle
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

  // from exe data
  val exe2mem = RegInit(0.U.asTypeOf(new EXE2MEMBundle))
  exe2mem := MuxCase(
    exe2mem,
    Seq(
      (io.exe2mem.valid && memAllowin) -> (io.exe2mem.bits),
      (!io.exe2mem.valid && memAllowin) -> (0.U.asTypeOf(new EXE2MEMBundle))
    )
  )
  val inst    = exe2mem.ifdata.inst
  val control = exe2mem.iddata.control

  // mem wrap
  val memReadWrap = Module(new DataMemReadWrap)
  memReadWrap.io.control     := control
  memReadWrap.io.rawReadData := io.globalmem.memData
  memReadWrap.io.addr        := exe2mem.exedata.aluResult
  val memData = memReadWrap.io.readData

  // to wb data
  val mem2wbData = Wire(new MEM2WBBundle)
  mem2wbData.ifdata          := exe2mem.ifdata
  mem2wbData.iddata          := exe2mem.iddata
  mem2wbData.exedata         := exe2mem.exedata
  mem2wbData.memdata.memData := memData

  io.mem2wb.bits := mem2wbData

  // mem2global
  io.mem2global.forward.enable := (control.wbOp =/= WriteBackOp.WB_NOP) && memValid
  io.mem2global.forward.wAddr  := inst(11, 7)
  io.mem2global.forward.wData := MuxCase(
    0.U,
    Seq(
      (control.wbOp === WriteBackOp.WB_ALU) -> (exe2mem.exedata.aluResult),
      (control.wbOp === WriteBackOp.WB_MEM) -> (memData)
    )
  )
}
