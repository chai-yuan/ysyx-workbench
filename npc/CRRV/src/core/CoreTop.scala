package core

import chisel3._
import chisel3.util._
import debug.DebugBundle
import core.IF._
import core.ID._
import core.EXE._
import core.MEM._
import core.WB._
import memory.AXIliteBundle
import memory.SRAMBundle

class CoreTop extends Module {
  val io = IO(new Bundle {
    val inst  = new AXIliteBundle
    val data  = new AXIliteBundle
    val debug = new DebugBundle
  })

  val preifStage = Module(new PreIF)
  val ifStage    = Module(new IF)
  val idStage    = Module(new ID)
  val exeStage   = Module(new EXE)
  val memStage   = Module(new MEM)
  val wbStage    = Module(new WB)

  preifStage.io.instMem <> io.inst.ar
  ifStage.io.instMem <> io.inst.r

  exeStage.io.dataMemAR <> io.data.ar
  exeStage.io.dataMemAW <> io.data.aw
  exeStage.io.dataMemW <> io.data.w
  memStage.io.dataMemR <> io.data.r
  memStage.io.dataMemB <> io.data.b

  // pipeline
  preifStage.io.preif2if <> ifStage.io.preif2if
  ifStage.io.if2id <> idStage.io.if2id
  idStage.io.id2exe <> exeStage.io.id2exe
  exeStage.io.exe2mem <> memStage.io.exe2mem
  memStage.io.mem2wb <> wbStage.io.mem2wb

  // global
  preifStage.io.pc     := ifStage.io.if2global.pc
  preifStage.io.branch := idStage.io.id2global.branch
  ifStage.io.branch    := idStage.io.id2global.branch

  idStage.io.writeBack  := wbStage.io.wb2global.writeBack
  idStage.io.exeForward := exeStage.io.exe2global.forward
  idStage.io.memForward := memStage.io.mem2global.forward
  idStage.io.exeMemLoad := exeStage.io.exe2global.exeMemLoad

  // debug
  io.debug.regs    := idStage.io.id2global.debugRegs
  io.debug.wbDebug := wbStage.io.wb2global.debug

  // 不使用端口
  io.inst.aw.valid := false.B
  io.inst.aw.addr  := 0.U
  io.inst.w.valid  := false.B
  io.inst.w.data   := 0.U
  io.inst.w.strb   := 0.U
  io.inst.b.ready  := false.B
}