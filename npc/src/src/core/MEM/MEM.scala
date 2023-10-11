package core.MEM

import chisel3._
import chisel3.util._
import core.EXE._
import core.MemBundle
import core.Hazerd2MEMBundle

class MEMBundle extends Bundle {
  val exe2mem    = Flipped(new EXE2MEMBundle)
  val hazerd2mem = Flipped(new Hazerd2MEMBundle)

  val mem2wb = new MEM2WBBundle

  val dataMem = new MemBundle
}

class MEM extends Module {
  val io = IO(new MEMBundle)

  val inst      = io.exe2mem.inst
  val aluResult = io.exe2mem.result
  val control   = io.exe2mem.control

  val mem2wb = Module(new MEM2WB)

  // mem
  io.dataMem.readEn    := false.B
  io.dataMem.writeEn   := false.B
  io.dataMem.addr      := 0.U
  io.dataMem.writeData := 0.U
  io.dataMem.mark      := "b1111".U
  // mem2wb
  mem2wb.io.memIn.aluResult := aluResult
  mem2wb.io.memIn.inst      := inst
  mem2wb.io.memIn.control   := control
  mem2wb.io.memIn.memResult := 0.U
  mem2wb.io.memFlush        := io.hazerd2mem.memFlush

  io.mem2wb := mem2wb.io.mem2wb
}
