package core.ID

import chisel3._
import chisel3.util._
import config.Config
import core.IF.IF2IDBundle
import core.WB.WB2IDBundle

class ID2Hazerd extends Bundle {
  val inst     = Output(UInt(32.W))
  val regData1 = Output(UInt(32.W))
  val regData2 = Output(UInt(32.W))
}

class IDBundle extends Bundle {
  val if2id = Flipped(new IF2IDBundle)
  val wb2id = Flipped(new WB2IDBundle)

  val id2hazerd = new ID2Hazerd
  val id2exe    = new ID2EXEBundle
}

class ID extends Module {
  val io = IO(new IDBundle)

  val id2exe = Module(new ID2EXE)

  val regs    = Module(new Registers)
  val control = Module(new Control)
  val immGen  = Module(new ImmGen)

  // if2id
  val pc   = io.if2id.pc
  val inst = io.if2id.inst
  // regs
  regs.io.raddr1 := inst(19, 15)
  regs.io.raddr2 := inst(24, 20)
  // control
  control.io.inst := inst
  // immGen
  immGen.io.inst     := inst
  immGen.io.instType := control.io.outControl.instType
  //id2hazerd
  //id2exe
}
