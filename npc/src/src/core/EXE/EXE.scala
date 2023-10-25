package core.EXE

import chisel3._
import chisel3.util._
import core.ID._
import core.MEM.MEMDataBundle
import config.AluSrcOp

class EXE extends Module {
  val io = IO(new Bundle {
    val dataMem = new MEMDataBundle
    val id2exe = Flipped(Decoupled(new ID2EXEBundle))
    val exe2mem = Decoupled(new EXE2MEMBundle)
    val exe2global = new EXE2GlobalBundle
  })
  // pipeline ctrl
  val readyGo = true.B
  val exeValid = RegInit(false.B)
  exeValid := Mux(exeAllowin, io.id2exe.valid, exeValid)
  val exeAllowin = !exeValid || (readyGo && memAllowin)
  val wbValid = exeValid && readyGo
  val memAllowin = io.exe2mem.ready

  io.exe2mem.valid := wbValid
  io.id2exe.ready := exeAllowin

  // from if data
  val id2exe = RegInit(0.U.asTypeOf(new ID2EXEBundle))
  id2exe := Mux(exeValid && exeAllowin, io.id2exe.bits, id2exe)
  val pc = id2exe.ifdata.pc
  val control = id2exe.iddata.control
  val regData1 = id2exe.iddata.reg1
  val regData2 = id2exe.iddata.reg2
  val imm = id2exe.iddata.imm

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
      (control.src1Op === AluSrcOp.SrcPC) -> (pc),
      (control.src1Op === AluSrcOp.SrcSeqPC) -> (pc + 4.U),
      (control.src1Op === AluSrcOp.SrcImm) -> (imm)
    )
  )
  val aluResult = alu.io.out

  // mem
  val memWrap = Module(new MemWrap)
  memWrap.io.dataMem <> io.dataMem
  memWrap.io.valid := exeValid
  memWrap.io.control <> control
  memWrap.io.addr := aluResult
  memWrap.io.writeData := regData2
  val readData = memWrap.io.readData

  // to mem data
  val exe2mem = Wire(new EXE2MEMBundle)
  exe2mem.ifdata <> io.id2exe.bits.ifdata
  exe2mem.iddata <> io.id2exe.bits.iddata
  exe2mem.exedata.aluResult := aluResult

  // exe2global
  io.exe2global.globalmem.memData := readData
}
