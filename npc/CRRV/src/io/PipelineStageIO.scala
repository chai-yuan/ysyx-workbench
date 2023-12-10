package io

import chisel3._
import chisel3.util._
import config.CPUconfig._
import core.define.OperationDefine._
import core.define.MemoryControlDefine._

class PipelineStageIO extends Bundle {
  def default() = 0.U.asTypeOf(this)
}

class FetchStageIO extends PipelineStageIO {
  val instValid = Bool()
  val pc        = UInt(ADDR_WIDTH.W)
}

class DecodeStageIO extends PipelineStageIO {
  val aluOp = UInt(ALU_OP_WIDTH.W)
  val mduOp = UInt(MDU_OP_WIDTH.W)
  val src1  = UInt(DATA_WIDTH.W)
  val src2  = UInt(DATA_WIDTH.W)

  val lsuOp   = UInt(LSU_OP_WIDTH.W)
  val lsuData = UInt(DATA_WIDTH.W)

  val regWen   = Bool()
  val regWaddr = UInt(5.W)
}

class ExecuteStageIO extends PipelineStageIO {
  val aluResult = UInt(DATA_WIDTH.W)
}

class MemoryStageIO extends PipelineStageIO {
  val memRead     = Bool()
  val memSign     = Bool()
  val memReadAddr = UInt(ADDR_WIDTH.W)
  val memReadLen  = UInt(LS_DATA_WIDTH.W)
}

class IF2IDIO extends PipelineStageIO {
  val IF = new FetchStageIO
}

class ID2EXEIO extends PipelineStageIO {
  val IF = new FetchStageIO
  val ID = new DecodeStageIO
}

class EXE2MEMIO extends PipelineStageIO {
  val IF  = new FetchStageIO
  val ID  = new DecodeStageIO
  val EXE = new ExecuteStageIO
}

class MEM2WBIO extends PipelineStageIO {
  val IF  = new FetchStageIO
  val ID  = new DecodeStageIO
  val EXE = new ExecuteStageIO
  val MEM = new MemoryStageIO
}