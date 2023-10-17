package core.EXE

import chisel3._
import chisel3.util._
import core.ID._
import core.Hazerd2EXEBundle
import core.Forward2EXEBundle

class EXE2ForwardBundle extends Bundle {
  val regSrc1 = Output(UInt(5.W))
  val regSrc2 = Output(UInt(5.W))
}

class EXEBundle extends Bundle {
  val id2exe      = Flipped(new ID2EXEBundle)
  val hazerd2exe  = Flipped(new Hazerd2EXEBundle)
  val exe2forward = new EXE2ForwardBundle
  val forward2exe = Flipped(new Forward2EXEBundle)

  val exe2mem = new EXE2MEMBundle
}

class EXE extends Module {
  val io = IO(new EXEBundle)

  val control = io.id2exe.control
  val inst    = io.id2exe.inst
  val imm     = io.id2exe.imm
  val pc      = io.id2exe.pc

  val alu     = Module(new ALU)
  val exe2mem = Module(new EXE2MEM)

  val reg1 = MuxCase(
    io.id2exe.reg1,
    Seq(
      (io.forward2exe.forward1Sel) -> (io.forward2exe.regData1)
    )
  )
  val reg2 = MuxCase(
    io.id2exe.reg2,
    Seq(
      (io.forward2exe.forward2Sel) -> (io.forward2exe.regData2)
    )
  )
  // alu
  alu.io.aluOp := control.aluOp
  alu.io.src1 := MuxCase(
    0.U,
    Seq(
      (control.src1PC_sel) -> pc,
      (control.src1Reg_sel) -> reg1
    )
  )
  alu.io.src2 := MuxCase(
    0.U,
    Seq(
      (control.src2Imm_sel) -> imm,
      (control.src2Reg_sel) -> reg2
    )
  )
  // exe2forward
  io.exe2forward.regSrc1 := inst(19, 15)
  io.exe2forward.regSrc2 := inst(24, 20)
  // exe2mem
  exe2mem.io.exeIn.control := control
  exe2mem.io.exeIn.inst    := inst
  exe2mem.io.exeIn.result  := alu.io.out
  exe2mem.io.exeIn.reg2    := io.id2exe.reg2
  exe2mem.io.exeIn.pc      := io.id2exe.pc
  exe2mem.io.exeIn.halt    := io.id2exe.halt

  exe2mem.io.exeFlush := io.hazerd2exe.exeFlush
  io.exe2mem          := exe2mem.io.exe2mem
}
