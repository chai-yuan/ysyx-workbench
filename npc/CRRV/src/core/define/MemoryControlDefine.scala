package core.define

import chisel3._
import chisel3.util._
import core.define.InstructionDefine._
import core.define.OperationDefine._

object  MemoryControlDefine{
val Y = true.B
val N = false.B
  // data width of load & store instructions
  val LS_DATA_WIDTH = log2Ceil(3)
  val LS_DATA_BYTE = 0.U(LS_DATA_WIDTH.W)
  val LS_DATA_HALF = 1.U(LS_DATA_WIDTH.W)
  val LS_DATA_WORD = 2.U(LS_DATA_WIDTH.W)

  // operations of ALU for AMO instructions
  val AMO_OP_WIDTH = log2Ceil(10)
  val AMO_OP_NOP  = 0.U(AMO_OP_WIDTH.W)
  val AMO_OP_SWAP = 1.U(AMO_OP_WIDTH.W)
  val AMO_OP_ADD  = 2.U(AMO_OP_WIDTH.W)
  val AMO_OP_XOR  = 3.U(AMO_OP_WIDTH.W)
  val AMO_OP_AND  = 4.U(AMO_OP_WIDTH.W)
  val AMO_OP_OR   = 5.U(AMO_OP_WIDTH.W)
  val AMO_OP_MIN  = 6.U(AMO_OP_WIDTH.W)
  val AMO_OP_MAX  = 7.U(AMO_OP_WIDTH.W)
  val AMO_OP_MINU = 8.U(AMO_OP_WIDTH.W)
  val AMO_OP_MAXU = 9.U(AMO_OP_WIDTH.W)

  // decode logic
  val DEFAULT =
  //                                               setEm                    fiTlb
  //                          en wen      width  signed |chkEm  amoOp    fi$ fd$ |fdTlb
  //                           |  |         |        |  |  |      |        |  |  |  |
                          List(N, N, LS_DATA_BYTE, N, N, N, AMO_OP_NOP,  N, N, N, N)
  val TABLE = Array(
    BitPat(LSU_LB)    ->  List(Y, N, LS_DATA_BYTE, Y, N, N, AMO_OP_NOP,  N, N, N, N),
    BitPat(LSU_LH)    ->  List(Y, N, LS_DATA_HALF, Y, N, N, AMO_OP_NOP,  N, N, N, N),
    BitPat(LSU_LW)    ->  List(Y, N, LS_DATA_WORD, N, N, N, AMO_OP_NOP,  N, N, N, N),
    BitPat(LSU_LBU)   ->  List(Y, N, LS_DATA_BYTE, N, N, N, AMO_OP_NOP,  N, N, N, N),
    BitPat(LSU_LHU)   ->  List(Y, N, LS_DATA_HALF, N, N, N, AMO_OP_NOP,  N, N, N, N),
    BitPat(LSU_SB)    ->  List(Y, Y, LS_DATA_BYTE, N, N, N, AMO_OP_NOP,  N, N, N, N),
    BitPat(LSU_SH)    ->  List(Y, Y, LS_DATA_HALF, N, N, N, AMO_OP_NOP,  N, N, N, N),
    BitPat(LSU_SW)    ->  List(Y, Y, LS_DATA_WORD, N, N, N, AMO_OP_NOP,  N, N, N, N),

    BitPat(LSU_LR)    ->  List(Y, N, LS_DATA_WORD, N, Y, N, AMO_OP_NOP,  N, N, N, N),
    BitPat(LSU_SC)    ->  List(Y, N, LS_DATA_WORD, N, N, Y, AMO_OP_NOP,  N, N, N, N),
    BitPat(LSU_SWAP)  ->  List(Y, N, LS_DATA_WORD, N, N, N, AMO_OP_SWAP, N, N, N, N),
    BitPat(LSU_ADD)   ->  List(Y, N, LS_DATA_WORD, N, N, N, AMO_OP_ADD,  N, N, N, N),
    BitPat(LSU_XOR)   ->  List(Y, N, LS_DATA_WORD, N, N, N, AMO_OP_XOR,  N, N, N, N),
    BitPat(LSU_AND)   ->  List(Y, N, LS_DATA_WORD, N, N, N, AMO_OP_AND,  N, N, N, N),
    BitPat(LSU_OR)    ->  List(Y, N, LS_DATA_WORD, N, N, N, AMO_OP_OR,   N, N, N, N),
    BitPat(LSU_MIN)   ->  List(Y, N, LS_DATA_WORD, N, N, N, AMO_OP_MIN,  N, N, N, N),
    BitPat(LSU_MAX)   ->  List(Y, N, LS_DATA_WORD, N, N, N, AMO_OP_MAX,  N, N, N, N),
    BitPat(LSU_MINU)  ->  List(Y, N, LS_DATA_WORD, N, N, N, AMO_OP_MINU, N, N, N, N),
    BitPat(LSU_MAXU)  ->  List(Y, N, LS_DATA_WORD, N, N, N, AMO_OP_MAXU, N, N, N, N),

    // BitPat(LSU_FENC)  ->  List(N, N, LS_DATA_BYTE, N, N, N, AMO_OP_NOP,  N, N, N, N),
    // BitPat(LSU_FENI)  ->  List(N, N, LS_DATA_BYTE, N, N, N, AMO_OP_NOP,  Y, Y, N, N),
    // BitPat(LSU_FENV)  ->  List(N, N, LS_DATA_BYTE, N, N, N, AMO_OP_NOP,  Y, Y, Y, Y),
  )
}
