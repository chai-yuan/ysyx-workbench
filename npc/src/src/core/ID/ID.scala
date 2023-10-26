package core.ID

import chisel3._
import chisel3.util._
import config.Config
import core.IF.IF2IDBundle
import core.WB.WriteBackBundle

class ID extends Module {
  val io = IO(new Bundle {
    val if2id     = Flipped(Decoupled(new IF2IDBundle))
    val id2exe    = Decoupled(new ID2EXEBundle)
    val id2global = new ID2GlobalBundle
    // global
    val exeForward = Flipped(new WriteBackBundle)
    val memForward = Flipped(new WriteBackBundle)
    val writeBack  = Flipped(new WriteBackBundle)
  })
  val branchSel = Wire(Bool())

  // pipeline ctrl
  val readyGo    = true.B
  val idValid    = RegInit(false.B)
  val exeAllowin = io.id2exe.ready
  val idAllowin  = !idValid || (readyGo && exeAllowin)
  idValid := MuxCase(
    idValid,
    Seq(
      (branchSel) -> (false.B),
      (idAllowin) -> (io.if2id.valid)
    )
  )
  val exeValid = idValid && readyGo

  io.id2exe.valid := exeValid
  io.if2id.ready  := idAllowin

  // from if data
  val if2id = RegInit(0.U.asTypeOf(new IF2IDBundle))
  if2id := Mux(io.if2id.valid && idAllowin, io.if2id.bits, if2id)
  val pc   = if2id.ifdata.pc
  val inst = if2id.ifdata.inst

  // control
  val control = Module(new Control)
  control.io.inst := inst
  val outControl = control.io.outControl
  // forward
  val forward = Module(new Forward)
  forward.io.addr1      := inst(19, 15)
  forward.io.addr2      := inst(24, 20)
  forward.io.exeForward := io.exeForward
  forward.io.memForward := io.memForward
  forward.io.writeBack  := io.writeBack
  val regData1 = forward.io.data1
  val regData2 = forward.io.data2
  // imm
  val immGen = Module(new ImmGen)
  immGen.io.inst     := inst
  immGen.io.instType := outControl.instType
  val imm = immGen.io.imm
  // branch
  val branch = Module(new Branch)
  branch.io.inst     := inst
  branch.io.regData1 := regData1
  branch.io.regData2 := regData2
  branch.io.imm      := imm
  branch.io.pc       := pc

  branchSel := branch.io.nextPCsel && idValid
  val branchTarget = branch.io.nextPC

  // to exe data
  val id2exeData = Wire(new ID2EXEBundle)
  id2exeData.ifdata         := if2id.ifdata
  id2exeData.iddata.control := outControl
  id2exeData.iddata.reg1    := regData1
  id2exeData.iddata.reg2    := regData2
  id2exeData.iddata.imm     := imm

  io.id2exe.bits := id2exeData
  // id2global
  io.id2global.branch.branchSel    := branchSel
  io.id2global.branch.branchTarget := branchTarget

  io.id2global.debugRegs := forward.io.debugRegs
}
