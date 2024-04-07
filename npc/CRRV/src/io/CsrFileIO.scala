package io

import chisel3._
import chisel3.util._
import config.CPUconfig._
import core.define.OperationDefine._
import core.define.CsrDefine._

class CsrReadIO extends Bundle {
  val op    = Output(UInt(CSR_OP_WIDTH.W))
  val valid = Input(Bool())
  val addr  = Output(UInt(CSR_ADDR_WIDTH.W))
  val data  = Input(UInt(DATA_WIDTH.W))
}

/**
  * 写入存在2种情况
  * 一种是正常的读写，另一种情况是发生异常
  */
class CsrWriteIO extends Bundle {
  val op   = Output(UInt(CSR_OP_WIDTH.W))
  val addr = Output(UInt(CSR_ADDR_WIDTH.W))
  val data = Output(UInt(DATA_WIDTH.W))

  val exceptType   = Output(UInt(EXC_TYPE_WIDTH.W))
  val exceptPc     = Output(UInt(ADDR_WIDTH.W))
  val exceptNextPc = Output(UInt(ADDR_WIDTH.W))
}

/**
  * 用于报告CSR写入，用于解决流水线冲突
  */
class Csr2HazardResolverIO extends Bundle {
  val op   = UInt(CSR_OP_WIDTH.W)
  val addr = UInt(CSR_ADDR_WIDTH.W)
}
