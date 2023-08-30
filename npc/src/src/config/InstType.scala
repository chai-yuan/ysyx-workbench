package config

import chisel3._

object InstType {
  val Inst_INV = 0.U
  val Inst_R   = 1.U
  val Inst_I   = 2.U
  val Inst_S   = 3.U
  val Inst_B   = 4.U
  val Inst_U   = 5.U
  val Inst_J   = 6.U
}
