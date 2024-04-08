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

    val read         = new SimpleInIO(DATA_WIDTH)
    val wb2csr       = new CsrWriteIO
    val excMonCommit = Output(new ExcMonCommitIO)
    val regForward   = Output(new RegForwardIO)
    val debug        = Output(new DebugInfoIO)
  })
  val if2wb  = io.mem2wb.IF
  val id2wb  = io.mem2wb.ID
  val exe2wb = io.mem2wb.EXE
  val mem2wb = io.mem2wb.MEM
  val addr   = io.mem2wb.MEM.memAddr

  val rdata = io.read.rdata
  val readData = MuxLookup(id2wb.lsuOp, 0.U)(
    Seq(
      LSU_LB -> (Cat(Fill(24, rdata(7)), rdata(7, 0))),
      LSU_LBU -> (Cat(0.U(24.W), rdata(7, 0))),
      LSU_LH -> (Cat(Fill(16, rdata(15)), rdata(15, 0))),
      LSU_LHU -> (Cat(0.U(16.W), rdata(15, 0))),
      LSU_LW -> (rdata),
      LSU_LR -> (rdata)
    )
  )

  val regData = MuxCase(
    exe2wb.exeResult,
    Seq(
      (mem2wb.amoValid) -> (mem2wb.amoResult),
      (id2wb.lsuOp =/= LSU_NOP) -> (readData)
    )
  )

  // 原子指令
  io.excMonCommit <> mem2wb.excMonCommit
  // csr
  io.wb2csr.op           := id2wb.csrOp
  io.wb2csr.addr         := id2wb.csrAddr
  io.wb2csr.data         := id2wb.csrWriteData
  io.wb2csr.exceptType   := id2wb.exceptType
  io.wb2csr.exceptPc     := if2wb.pc
  io.wb2csr.exceptNextPc := id2wb.nextpc
  // 写入寄存器组
  io.regForward.en   := id2wb.regWen
  io.regForward.addr := id2wb.regWaddr
  io.regForward.data := regData
  io.regForward.load := exe2wb.load
  // debug
  io.debug.valid        := if2wb.instValid
  io.debug.halt         := id2wb.inst === EBREAK || id2wb.exceptType === EXC_ILLEG
  io.debug.deviceAccess := id2wb.lsuOp =/= LSU_NOP
  io.debug.deviceAddr   := exe2wb.exeResult
  io.debug.pc           := if2wb.pc
}
