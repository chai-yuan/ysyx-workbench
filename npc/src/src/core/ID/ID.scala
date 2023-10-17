package core.ID

import chisel3._
import chisel3.util._
import config.Config
import core.IF.IF2IDBundle
import core.WB.WB2IDBundle
import core.Hazerd2IDBundle

class ID2HazerdBundle extends Bundle {
  val inst     = Output(UInt(32.W))
  val pc       = Output(UInt(32.W))
  val imm      = Output(UInt(32.W))
  val regData1 = Output(UInt(32.W))
  val regData2 = Output(UInt(32.W))
}

class IDBundle extends Bundle {
  val if2id = Flipped(new IF2IDBundle)
  val wb2id = Flipped(new WB2IDBundle)

  val hazerd2id = Flipped(new Hazerd2IDBundle)

  val id2hazerd = new ID2HazerdBundle
  val id2exe    = new ID2EXEBundle
  // debug
  val debugRegs = Output(Vec(32, UInt(32.W)))
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
  regs.io.wen    := io.wb2id.regwen
  regs.io.waddr  := io.wb2id.regaddr
  regs.io.wdata  := io.wb2id.regwdata
  regs.io.raddr1 := inst(19, 15)
  regs.io.raddr2 := inst(24, 20)
  val regData1 = regs.io.rdata1
  val regData2 = regs.io.rdata2
  // control
  control.io.inst := inst
  val outControl = control.io.outControl
  // immGen
  immGen.io.inst     := inst
  immGen.io.instType := outControl.instType
  val imm = immGen.io.imm
  //id2hazerd
  io.id2hazerd.inst     := inst
  io.id2hazerd.imm      := imm
  io.id2hazerd.pc       := pc
  io.id2hazerd.regData1 := regData1
  io.id2hazerd.regData2 := regData2
  //id2exe
  id2exe.io.idIn.control := outControl
  id2exe.io.idIn.inst    := inst
  id2exe.io.idIn.reg1    := regData1
  id2exe.io.idIn.reg2    := regData2
  id2exe.io.idIn.imm     := imm
  id2exe.io.idIn.pc      := pc
  id2exe.io.idFlush      := io.hazerd2id.idFlush

  io.id2exe := id2exe.io.id2exe
  // debug
  io.debugRegs        := regs.io.debug
  id2exe.io.idIn.halt := (inst(6, 0) === "b1110011".U && imm === 1.U)
}
