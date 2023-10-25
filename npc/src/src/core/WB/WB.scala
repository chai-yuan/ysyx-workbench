package core.WB

import chisel3._
import chisel3.util._
import config.Config
import core.MEM.MEM2WBBundle
import config.WriteBackOp
import core.ID.ControlBundle

class WB extends Module {
  val io = IO(new Bundle {
    val mem2wb    = Flipped(Decoupled(new MEM2WBBundle))
    val wb2global = new WB2GlobalBundle
  })

  // pipeline ctrl
  val readyGo   = true.B
  val wbValid   = RegInit(false.B)
  val wbAllowin = !wbValid && readyGo
  wbValid := Mux(wbAllowin, io.mem2wb.valid, wbValid)

  io.mem2wb.ready := wbAllowin

  // from mem data
  val mem2wb = RegInit(0.U.asTypeOf(new MEM2WBBundle))
  mem2wb := Mux(wbValid && wbAllowin, io.mem2wb.bits, mem2wb)
  val control   = mem2wb.iddata.control
  val inst      = mem2wb.ifdata.inst
  val wbAddr    = inst(11, 7)
  val aluResult = mem2wb.exedata.aluResult
  val memResult = mem2wb.memdata.memData

  // wb2global
  io.wb2global.writeBack.enable := wbValid && control.wbOp =/= WriteBackOp.WB_NOP
  io.wb2global.writeBack.wAddr  := wbAddr
  io.wb2global.writeBack.wData := MuxCase(
    0.U,
    Seq(
      (control.wbOp === WriteBackOp.WB_ALU) -> (aluResult),
      (control.wbOp === WriteBackOp.WB_MEM) -> (memResult)
    )
  )

  io.wb2global.debug.valid := wbValid
  io.wb2global.debug.inst  := inst
  io.wb2global.debug.halt  := control.halt
}
