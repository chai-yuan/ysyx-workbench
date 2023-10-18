package core.MEM

import chisel3._
import chisel3.util._
import core.EXE._
import core.MemBundle
import core.Hazerd2MEMBundle
import core.ID.ControlBundle
import core.Forward2MEMBundle

class MEMBundle extends Bundle {
  val exe2mem     = Flipped(new EXE2MEMBundle)
  val hazerd2mem  = Flipped(new Hazerd2MEMBundle)
  val mem2forward = new MEM2ForwardBundle
  val forward2mem = Flipped(new Forward2MEMBundle)

  val mem2wb = new MEM2WBBundle

  val dataMem  = new MemBundle
  val readData = Output(UInt(32.W))
}

class MEM2ForwardBundle extends Bundle {
  val enable = Output(Bool())
  val addr   = Output(UInt(32.W))
  val data   = Output(UInt(32.W))

  val regSrc2 = Output(UInt(5.W))
}

class MEM extends Module {
  val io = IO(new MEMBundle)

  val inst      = io.exe2mem.inst
  val aluResult = io.exe2mem.result
  val control   = io.exe2mem.control

  val mem2wb = Module(new MEM2WB)

  val memWrap = Module(new MemWrap)

  // mem
  val reg2 = MuxCase(
    io.exe2mem.reg2,
    Seq(
      (io.forward2mem.forward2Sel) -> (io.forward2mem.regData2)
    )
  )
  memWrap.io.dataMem <> io.dataMem
  memWrap.io.control <> control
  memWrap.io.addr      := aluResult
  memWrap.io.writeData := reg2
  io.readData          := memWrap.io.readData
  // forward
  io.mem2forward.enable  := control.wbEn
  io.mem2forward.addr    := inst(11, 7)
  io.mem2forward.data    := aluResult
  io.mem2forward.regSrc2 := inst(24, 20)
  // mem2wb
  mem2wb.io.memIn.aluResult := aluResult
  mem2wb.io.memIn.inst      := inst
  mem2wb.io.memIn.control   := control
  mem2wb.io.memIn.pc        := io.exe2mem.pc
  mem2wb.io.memIn.halt      := io.exe2mem.halt

  mem2wb.io.memFlush := io.hazerd2mem.memFlush
  io.mem2wb          := mem2wb.io.mem2wb
}
