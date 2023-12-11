package core.pipeline

import chisel3._
import chisel3.util._
import config.CPUconfig._
import core.define.InstructionDefine._
import core.define.OperationDefine._
import core.define.MemoryControlDefine._
import io._

class WriteBackStage extends Module {
  val io = IO(new Bundle {
    val mem2wb = Input(new MEM2WBIO)

    val readData   = Input(UInt(DATA_WIDTH.W))
    val regForward = Output(new RegForwardIO)
    val debug      = new DebugIO
  })
  val if2wb  = io.mem2wb.IF
  val id2wb  = io.mem2wb.ID
  val exe2wb = io.mem2wb.EXE
  val mem2wb = io.mem2wb.MEM
  val addr   = io.mem2wb.MEM.memAddr

  val shiftData = io.readData >> (Cat(addr(1, 0), 0.U(3.W)))
  val readData = MuxLookup(id2wb.lsuOp, 0.U)(
    Seq(
      LSU_LB -> (Cat(Fill(24, shiftData(7)), shiftData(7, 0))),
      LSU_LBU -> (Cat(0.U(24.W), shiftData(7, 0))),
      LSU_LH -> (Cat(Fill(16, shiftData(15)), shiftData(15, 0))),
      LSU_LHU -> (Cat(0.U(16.W), shiftData(15, 0))),
      LSU_LW -> (shiftData)
    )
  )

  val regData = Mux(id2wb.lsuOp =/= LSU_NOP, readData, exe2wb.aluResult)

  // 写入寄存器组
  io.regForward.en   := id2wb.regWen
  io.regForward.addr := id2wb.regWaddr
  io.regForward.data := regData
  io.regForward.load := exe2wb.load
  // debug
  io.debug.validInst := id2wb.inst =/= NOP && id2wb.inst =/= 0.U
  io.debug.halt      := id2wb.inst === EBREAK
  io.debug.skipIO    := id2wb.lsuOp =/= LSU_NOP

  io.debug.pc       := if2wb.pc
  io.debug.regWen   := id2wb.regWen
  io.debug.regWaddr := id2wb.regWaddr
  io.debug.regWdata := regData
}
