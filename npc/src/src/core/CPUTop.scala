package core

import chisel3._
import chisel3.util._
import debug._
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

  val IF_stage  = Module(new IF)
  val ID_stage  = Module(new ID)
  val EXE_stage = Module(new EXE)
  val MEM_stage = Module(new MEM)
  val WB_stage  = Module(new WB)

  val hazardDetection = Module(new HazardDetection)
  val forward         = Module(new Forward)
  // mem
  IF_stage.io.instMem <> io.inst
  MEM_stage.io.dataMem <> io.data
  // hazard
  IF_stage.io.hazerd2if <> hazardDetection.io.hazard2if
  ID_stage.io.hazerd2id <> hazardDetection.io.hazerd2id
  EXE_stage.io.hazerd2exe <> hazardDetection.io.hazerd2exe
  MEM_stage.io.hazerd2mem <> hazardDetection.io.hazerd2mem

  ID_stage.io.id2hazerd <> hazardDetection.io.id2hazerd
  // forward
  forward.io.forward2exe <> EXE_stage.io.forward2exe
  forward.io.exe2forward <> EXE_stage.io.exe2forward
  forward.io.mem2forward <> MEM_stage.io.mem2forward
  forward.io.wb2forward <> WB_stage.io.wb2forward
  // pipe line
  ID_stage.io.if2id <> IF_stage.io.if2id
  EXE_stage.io.id2exe <> ID_stage.io.id2exe
  MEM_stage.io.exe2mem <> EXE_stage.io.exe2mem
  WB_stage.io.mem2wb <> MEM_stage.io.mem2wb
  WB_stage.io.readData := MEM_stage.io.readData

  ID_stage.io.wb2id <> WB_stage.io.wb2id
  // debug
  io.debug.pc   := WB_stage.io.debugPc
  io.debug.regs := ID_stage.io.debugRegs
  io.debug.halt := ID_stage.io.debugHalt
}
