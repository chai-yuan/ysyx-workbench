package core.EXE

import chisel3._
import chisel3.util._
import core.ID._
import core.MemBundle
import config.AluSrcOp
import config.WriteBackOp

class EXE extends Module {
  val io = IO(new Bundle {
    val dataMem    = new MemBundle
    val id2exe     = Flipped(Decoupled(new ID2EXEBundle))
    val exe2mem    = Decoupled(new EXE2MEMBundle)
    val exe2global = new EXE2GlobalBundle
  })
  // pipeline ctrl
  val readyGo    = true.B
  val exeValid   = RegInit(false.B)
  val memAllowin = io.exe2mem.ready
  val exeAllowin = !exeValid || (readyGo && memAllowin)
  exeValid := Mux(exeAllowin, io.id2exe.valid, exeValid)
  val wbValid = exeValid && readyGo

  io.exe2mem.valid := wbValid
  io.id2exe.ready  := exeAllowin

  // from if data
  val id2exe = RegInit(0.U.asTypeOf(new ID2EXEBundle))
  id2exe := Mux(io.id2exe.valid && exeAllowin, io.id2exe.bits, id2exe)
  val pc       = id2exe.ifdata.pc
  val inst     = id2exe.ifdata.inst
  val control  = id2exe.iddata.control
  val regData1 = id2exe.iddata.reg1
  val regData2 = id2exe.iddata.reg2
  val imm      = id2exe.iddata.imm

  // alu
  val alu = Module(new ALU)
  alu.io.aluOp := control.aluOp
  alu.io.src1 := MuxCase(
    regData1,
    Seq(
      (control.src1Op === AluSrcOp.SrcPC) -> (pc),
      (control.src1Op === AluSrcOp.SrcSeqPC) -> (pc + 4.U),
      (control.src1Op === AluSrcOp.SrcImm) -> (imm)
    )
  )
  alu.io.src2 := MuxCase(
    regData2,
    Seq(
      (control.src2Op === AluSrcOp.SrcPC) -> (pc),
      (control.src2Op === AluSrcOp.SrcSeqPC) -> (pc + 4.U),
      (control.src2Op === AluSrcOp.SrcImm) -> (imm)
    )
  )
  val aluResult = alu.io.out

  // mem
  val memWrap = Module(new MemWrap)
  memWrap.io.dataMem <> io.dataMem
  memWrap.io.control <> control
  memWrap.io.valid     := exeValid
  memWrap.io.addr      := aluResult
  memWrap.io.writeData := regData2
  val readData = memWrap.io.readData

  // to mem data
  val exe2mem = Wire(new EXE2MEMBundle)
  exe2mem.ifdata            := id2exe.ifdata
  exe2mem.iddata            := id2exe.iddata
  exe2mem.exedata.aluResult := aluResult

  io.exe2mem.bits := exe2mem

  // exe2global
  io.exe2global.globalmem.memData := readData

  io.exe2global.forward.enable := (control.wbOp === WriteBackOp.WB_ALU) && exeValid
  io.exe2global.forward.wAddr  := inst(11, 7)
  io.exe2global.forward.wData  := aluResult
}
