package core.EXE

import chisel3._
import chisel3.util._
import core.ID._
import core.Hazerd2EXEBundle

class EXEBundle extends Bundle {
  val id2exe     = Flipped(new ID2EXEBundle)
  val hazerd2exe = Flipped(new Hazerd2EXEBundle)

  val exe2mem = new EXE2MEMBundle
}

class EXE extends Module {
  val io = IO(new EXEBundle)

  val control = io.id2exe.control
  val inst    = io.id2exe.inst
  val imm     = io.id2exe.imm

  val alu     = Module(new ALU)
  val exe2mem = Module(new EXE2MEM)

  // alu
  alu.io.aluOp := control.aluOp
  alu.io.src1  := io.id2exe.reg1
  alu.io.src2  := io.id2exe.reg2
  // exe2mem
  exe2mem.io.exeIn.control := control
  exe2mem.io.exeIn.inst    := inst
  exe2mem.io.exeIn.result  := alu.io.out
  exe2mem.io.exeIn.reg2    := io.id2exe.reg2
  exe2mem.io.exeIn.pc      := io.id2exe.pc

  exe2mem.io.exeFlush := io.hazerd2exe.exeFlush
  io.exe2mem          := exe2mem.io.exe2mem
}
