package config

import chisel3._
import chisel3.util._

object Config {
  def PCinit = "h8000_0000".U(32.W)
}

object AluOp {
  def AluOpWidth = 7.W
  val ALU_NOP    = 0.U(AluOpWidth)
  val ALU_PSV    = 1.U(AluOpWidth)
  val ALU_ADD    = 2.U(AluOpWidth)
  val ALU_SLT    = 3.U(AluOpWidth)
  val ALU_SLTU   = 4.U(AluOpWidth)
  val ALU_XOR    = 5.U(AluOpWidth)
  val ALU_OR     = 6.U(AluOpWidth)
  val ALU_AND    = 7.U(AluOpWidth)
  val ALU_SLL    = 8.U(AluOpWidth)
  val ALU_SRL    = 9.U(AluOpWidth)
  val ALU_SRA    = 10.U(AluOpWidth)
  val ALU_SUB    = 11.U(AluOpWidth)
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
