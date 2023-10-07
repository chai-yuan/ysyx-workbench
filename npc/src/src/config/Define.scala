package config

import chisel3._
import chisel3.util._

object Config {
  def PCinit = "h8000_0000".U(32.W)
}

object AluOp {}

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
  val CSR_MSTATUS = "h300".U
  val CSR_MTVEC   = "h305".U
  val CSR_MEPC    = "h341".U
  val CSR_MCAUSE  = "h342".U
}
