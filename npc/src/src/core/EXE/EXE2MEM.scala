package core.EXE

import chisel3._
import chisel3.util._
import core.ID._

class EXE2MEMBundle extends Bundle {
  val control = new ControlBundle

  val result = Output(UInt(32.W))
  val reg2   = Output(UInt(32.W))
  val inst   = Output(UInt(32.W))
  val pc     = Output(UInt(32.W))
  // debug
  val halt = Output(Bool())
}

class EXE2MEM extends Module {
  val io = IO(new Bundle {
    val exe2mem  = new EXE2MEMBundle
    val exeIn    = Flipped(new EXE2MEMBundle)
    val exeFlush = Input(Bool())
  })
  val zeroWire = Wire(new EXE2MEMBundle)
  zeroWire := 0.U.asTypeOf(new EXE2MEMBundle)

  val regs = RegNext(Mux(io.exeFlush, zeroWire, io.exeIn))

  io.exe2mem := regs
}
