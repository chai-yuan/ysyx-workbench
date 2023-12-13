package core.define

import chisel3._
import chisel3.util._

object OperationDefine {
  // ALU操作数选择
  val OPR_WIDTH = log2Ceil(9)
  val OPR_ZERO  = 0.U(OPR_WIDTH.W)
  val OPR_REG1  = 1.U(OPR_WIDTH.W)
  val OPR_REG2  = 2.U(OPR_WIDTH.W)
  val OPR_IMMI  = 3.U(OPR_WIDTH.W)
  val OPR_IMMS  = 4.U(OPR_WIDTH.W)
  val OPR_IMMU  = 5.U(OPR_WIDTH.W)
  val OPR_IMMR  = 6.U(OPR_WIDTH.W)
  val OPR_PC    = 7.U(OPR_WIDTH.W)
  val OPR_4     = 8.U(OPR_WIDTH.W)

  // ALU运算符
  val ALU_OP_WIDTH = log2Ceil(10)
  val ALU_ADD      = 0.U(ALU_OP_WIDTH.W)
  val ALU_SUB      = 1.U(ALU_OP_WIDTH.W)
  val ALU_XOR      = 2.U(ALU_OP_WIDTH.W)
  val ALU_OR       = 3.U(ALU_OP_WIDTH.W)
  val ALU_AND      = 4.U(ALU_OP_WIDTH.W)
  val ALU_SLT      = 5.U(ALU_OP_WIDTH.W)
  val ALU_SLTU     = 6.U(ALU_OP_WIDTH.W)
  val ALU_SLL      = 7.U(ALU_OP_WIDTH.W)
  val ALU_SRL      = 8.U(ALU_OP_WIDTH.W)
  val ALU_SRA      = 9.U(ALU_OP_WIDTH.W)

  // 乘除法运算符
  val MDU_OP_WIDTH = log2Ceil(9)
  val MDU_NOP      = 0.U(MDU_OP_WIDTH.W)
  val MDU_MUL      = 1.U(MDU_OP_WIDTH.W)
  val MDU_MULH     = 2.U(MDU_OP_WIDTH.W)
  val MDU_MULHSU   = 3.U(MDU_OP_WIDTH.W)
  val MDU_MULHU    = 4.U(MDU_OP_WIDTH.W)
  val MDU_DIV      = 5.U(MDU_OP_WIDTH.W)
  val MDU_DIVU     = 6.U(MDU_OP_WIDTH.W)
  val MDU_REM      = 7.U(MDU_OP_WIDTH.W)
  val MDU_REMU     = 8.U(MDU_OP_WIDTH.W)

  // 分支跳转操作数选择
  val BR_WIDTH = log2Ceil(8)
  val BR_NOP   = 0.U(BR_WIDTH.W)
  val BR_AL    = 1.U(BR_WIDTH.W)
  val BR_EQ    = 2.U(BR_WIDTH.W)
  val BR_NE    = 3.U(BR_WIDTH.W)
  val BR_LT    = 4.U(BR_WIDTH.W)
  val BR_GE    = 5.U(BR_WIDTH.W)
  val BR_LTU   = 6.U(BR_WIDTH.W)
  val BR_GEU   = 7.U(BR_WIDTH.W)

  // 访存操作符
  val LSU_OP_WIDTH = log2Ceil(9)
  val LSU_NOP      = 0.U(LSU_OP_WIDTH.W)
  val LSU_LB       = 1.U(LSU_OP_WIDTH.W)
  val LSU_LH       = 2.U(LSU_OP_WIDTH.W)
  val LSU_LW       = 3.U(LSU_OP_WIDTH.W)
  val LSU_LBU      = 4.U(LSU_OP_WIDTH.W)
  val LSU_LHU      = 5.U(LSU_OP_WIDTH.W)
  val LSU_SB       = 6.U(LSU_OP_WIDTH.W)
  val LSU_SH       = 7.U(LSU_OP_WIDTH.W)
  val LSU_SW       = 8.U(LSU_OP_WIDTH.W)

  // CSR 操作符
  val CSR_OP_WIDTH = log2Ceil(6)
  val CSR_NOP      = 0.U(CSR_OP_WIDTH.W)
  val CSR_R        = 1.U(CSR_OP_WIDTH.W)
  val CSR_W        = 2.U(CSR_OP_WIDTH.W)
  val CSR_RW       = 3.U(CSR_OP_WIDTH.W)
  val CSR_RS       = 4.U(CSR_OP_WIDTH.W)
  val CSR_RC       = 5.U(CSR_OP_WIDTH.W)

  // 异常类型
  val EXC_TYPE_WIDTH = log2Ceil(8)
  val EXC_NONE       = 0.U(EXC_TYPE_WIDTH.W)
  val EXC_ECALL      = 1.U(EXC_TYPE_WIDTH.W)
  val EXC_MRET       = 2.U(EXC_TYPE_WIDTH.W)
  val EXC_ILLEG      = 7.U(EXC_TYPE_WIDTH.W) // 非法指令
}
