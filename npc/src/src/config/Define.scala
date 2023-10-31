package config

import chisel3._
import chisel3.util._

object Config {
  def PCinit = "h7FFF_FFFC".U(32.W)
}

object AluSrcOp {
  def AluSrcOpWidth = 3.W
  val SrcReg        = 0.U(AluSrcOpWidth)
  val SrcPC         = 1.U(AluSrcOpWidth)
  val SrcSeqPC      = 2.U(AluSrcOpWidth)
  val SrcImm        = 3.U(AluSrcOpWidth)
  val SrcCSR        = 4.U(AluSrcOpWidth)
}

object AluOp {
  def AluOpWidth = 4.W
  val ALU_NOP    = 0.U(AluOpWidth)
  val ALU_ADD    = 1.U(AluOpWidth)
  val ALU_SLT    = 2.U(AluOpWidth)
  val ALU_SLTU   = 3.U(AluOpWidth)
  val ALU_XOR    = 4.U(AluOpWidth)
  val ALU_OR     = 5.U(AluOpWidth)
  val ALU_AND    = 6.U(AluOpWidth)
  val ALU_SLL    = 7.U(AluOpWidth)
  val ALU_SRL    = 8.U(AluOpWidth)
  val ALU_SRA    = 9.U(AluOpWidth)
  val ALU_SUB    = 10.U(AluOpWidth)
}

object MemOp {
  def MemOpWidth = 3.W
  val MEM_NOP    = 0.U(MemOpWidth)
  val MEM_B      = 1.U(MemOpWidth)
  val MEM_BU     = 2.U(MemOpWidth)
  val MEM_H      = 3.U(MemOpWidth)
  val MEM_HU     = 4.U(MemOpWidth)
  val MEM_W      = 5.U(MemOpWidth)
}

object WriteBackOp {
  def WriteBackOpWidth = 2.W
  val WB_NOP           = 0.U(WriteBackOpWidth)
  val WB_ALU           = 1.U(WriteBackOpWidth)
  val WB_MEM           = 2.U(WriteBackOpWidth)
}

object InstType {
  def InstTypeWidth = 4.W
  val instR         = 0.U(InstTypeWidth)
  val instI         = 1.U(InstTypeWidth)
  val instS         = 2.U(InstTypeWidth)
  val instB         = 3.U(InstTypeWidth)
  val instU         = 4.U(InstTypeWidth)
  val instJ         = 5.U(InstTypeWidth)
}

object CSRCodes {
  def CSRCodesWidth = 12.W
  val CSR_MSTATUS   = "h300".U(CSRCodesWidth)
  val CSR_MTVEC     = "h305".U(CSRCodesWidth)
  val CSR_MEPC      = "h341".U(CSRCodesWidth)
  val CSR_MCAUSE    = "h342".U(CSRCodesWidth)
}
