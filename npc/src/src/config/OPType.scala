package config

import chisel3._

object OPType {
  val OP_NOP = "b0000".U
  val OP_ADD = "b0001".U
  val OP_SUB = "b0010".U
  val OP_AND = "b0100".U
  val OP_OR  = "b0101".U
  val OP_XOR = "b0111".U
  val OP_SLL = "b1000".U
  val OP_SRL = "b1001".U
  val OP_SRA = "b1011".U
  val OP_EQ  = "b1100".U
  val OP_NEQ = "b1101".U
  val OP_LT  = "b1110".U
  val OP_GE  = "b1111".U
}
