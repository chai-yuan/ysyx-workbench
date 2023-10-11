package core.MEM

import chisel3._
import chisel3.util._
import core.EXE._
import core.MemBundle

class MEMBundle extends Bundle {
  val id2mem   = Flipped(new EXE2MEMBundle)
  val mem2wb   = new MEM2WBBundle
  val memFlush = Input(Bool())

  val dataMem = new MemBundle
}

class MEM extends Module {
  val io = IO(new MEMBundle)

  val inst      = io.id2mem.inst
  val aluResult = io.id2mem.result
  val control   = io.id2mem.control

  val mem2wb = Module(new MEM2WB)

  // mem2wb
  mem2wb.io.memIn.aluResult  := aluResult
  mem2wb.io.memIn.inst       := inst
  mem2wb.io.memIn.control    := control
  mem2wb.io.mem2wb.memResult := 0.U
  io.mem2wb                  := mem2wb.io.mem2wb
}
