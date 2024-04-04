package core.define

import chisel3._
import chisel3.util._
import core.define.OperationDefine._

object CsrDefine {
  val CSR_MODE_WIDTH = 2
  val CSR_MODE_U     = "b00".U(CSR_MODE_WIDTH.W)
  val CSR_MODE_S     = "b01".U(CSR_MODE_WIDTH.W)
  val CSR_MODE_M     = "b11".U(CSR_MODE_WIDTH.W)

  val CSR_ADDR_WIDTH = 12

  val CSR_MSTATUS    = 0x300.U(CSR_ADDR_WIDTH.W)
  val CSR_MISA       = 0x301.U(CSR_ADDR_WIDTH.W)
  val CSR_MEDELEG    = 0x302.U(CSR_ADDR_WIDTH.W)
  val CSR_MIDELEG    = 0x303.U(CSR_ADDR_WIDTH.W)
  val CSR_MIE        = 0x304.U(CSR_ADDR_WIDTH.W)
  val CSR_MTVEC      = 0x305.U(CSR_ADDR_WIDTH.W)
  val CSR_MCOUNTEREN = 0x306.U(CSR_ADDR_WIDTH.W)

  val CSR_MSCRATCH = 0x340.U(CSR_ADDR_WIDTH.W)
  val CSR_MEPC     = 0x341.U(CSR_ADDR_WIDTH.W)
  val CSR_MCAUSE   = 0x342.U(CSR_ADDR_WIDTH.W)
  val CSR_MTVAL    = 0x343.U(CSR_ADDR_WIDTH.W)
  val CSR_MIP      = 0x344.U(CSR_ADDR_WIDTH.W)

  val CSR_MVENDORID = 0xf11.U(CSR_ADDR_WIDTH.W)
  val CSR_MARCHID   = 0xf12.U(CSR_ADDR_WIDTH.W)
}
