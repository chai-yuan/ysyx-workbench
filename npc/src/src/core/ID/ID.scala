package core.ID

import chisel3._
import chisel3.util._
import config.Config
import core.IF.IF2IDBundle
import core.WB.WriteBackBundle

class ID extends Module {
  val io = IO(new Bundle {
    val if2id = Flipped(Decoupled(new IF2IDBundle))
    val id2exe = Decoupled(new ID2EXEBundle)
    val id2global = new ID2GlobalBundle
    // global
    val writeBack = Flipped(new WriteBackBundle)
  })

  // pipeline ctrl
  val readyGo = true.B
  val idValid = RegInit(false.B)
  idValid := Mux(idAllowin, io.if2id.valid, idValid)
  val idAllowin = !idValid || (readyGo && exeAllowin)
  val exeValid = idValid && readyGo
  val exeAllowin = io.if2id.ready

  io.id2exe.valid := exeValid
  io.if2id.ready := idAllowin

  // from if data
  val if2id = RegInit(0.U.asTypeOf(new IF2IDBundle))
  if2id := Mux(idValid && idAllowin, io.if2id.bits, if2id)
  val pc = if2id.ifdata.pc
  val inst = if2id.ifdata.inst

  // control
  val control = Module(new Control)
  control.io.inst := inst
  val outControl = control.io.outControl
  // regs
  val regs = Module(new Registers)
  regs.io.raddr1 := inst(19, 15)
  regs.io.raddr2 := inst(24, 20)
  regs.io.wen := io.writeBack.enable
  regs.io.waddr := io.writeBack.wAddr
  regs.io.wdata := io.writeBack.wData
  val regData1 = regs.io.rdata1
  val regData2 = regs.io.rdata2
  // imm
  val immGen = Module(new ImmGen)
  immGen.io.inst := inst
  immGen.io.instType := outControl.instType
  val imm = immGen.io.imm
  // branch
  val branch = Module(new Branch)
  branch.io.inst := inst
  branch.io.regData1 := regData1
  branch.io.regData2 := regData2
  branch.io.imm := imm
  branch.io.pc := pc
  val branchSel = branch.io.nextPCsel
  val branchTarget = branch.io.nextPC

  // to exe data
  val id2exeData = Wire(new ID2EXEBundle)
  id2exeData.ifdata <> io.if2id.bits.ifdata
  id2exeData.iddata.control <> outControl
  id2exeData.iddata.reg1 := regData1
  id2exeData.iddata.reg2 := regData2
  id2exeData.iddata.imm := imm

  io.id2exe.bits <> id2exeData
  // id2global
  io.id2global.branch.branchSel := branchSel
  io.id2global.branch.branchTarget := branchTarget

  io.id2global.debugRegs := regs.io.debug
}
