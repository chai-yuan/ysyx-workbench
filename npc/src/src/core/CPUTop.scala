package core

import chisel3._
import chisel3.util._
import debug.DebugBundle
import core.IF._
import core.ID._
import core.EXE._
import core.MEM._
import core.WB._

class MemBundle extends Bundle {
  val addr      = Output(UInt(32.W))
  val writeEn   = Output(Bool())
  val writeData = Output(UInt(32.W))
  val readEn    = Output(Bool())
  val readData  = Input(UInt(32.W))
  val mark      = Output(UInt(4.W))
}

class CPUTop extends Module {
  val io = IO(new Bundle {
    val inst  = new MemBundle
    val data  = new MemBundle
    val debug = new DebugBundle
  })

  val preifStage = Module(new PreIF)
  val ifStage    = Module(new IF)
  val idStage    = Module(new ID)
  val exeStage   = Module(new EXE)
  val memStage   = Module(new MEM)
  val wbStage    = Module(new WB)

  preifStage.io.instMem <> io.inst
  exeStage.io.dataMem <> io.data

  // pipeline
  preifStage.io.preif2if <> ifStage.io.preif2if
  ifStage.io.if2id <> idStage.io.if2id
  idStage.io.id2exe <> exeStage.io.id2exe
  exeStage.io.exe2mem <> memStage.io.exe2mem
  memStage.io.mem2wb <> wbStage.io.mem2wb

  // global
  preifStage.io.pc      := ifStage.io.if2global.pc
  preifStage.io.branch  := idStage.io.id2global.branch
  idStage.io.writeBack  := wbStage.io.wb2global.writeBack
  idStage.io.exeForward := exeStage.io.exe2global.forward
  idStage.io.memForward := memStage.io.mem2global.forward
  memStage.io.globalmem := exeStage.io.exe2global.globalmem

  // debug
  io.debug.regs    := idStage.io.id2global.debugRegs
  io.debug.wbDebug := wbStage.io.wb2global.debug
}
