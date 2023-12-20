package core.muldiv

import chisel3._
import chisel3.util._
import io._
import config.CPUconfig._
import core.define.OperationDefine._

class Divider extends Module {
  val io = IO(new Bundle {
    val en    = Input(Bool())
    val flush = Input(Bool())
    val valid = Output(Bool())

    val sign      = Input(Bool())
    val opr1      = Input(UInt(DATA_WIDTH.W))
    val opr2      = Input(UInt(DATA_WIDTH.W))
    val divresult = Output(UInt(DATA_WIDTH.W))
    val remresult = Output(UInt(DATA_WIDTH.W))
  })
  // 记录结果的符号状态
  val opr1Neg      = io.sign && io.opr1(DATA_WIDTH - 1)
  val opr2Neg      = io.sign && io.opr2(DATA_WIDTH - 1)
  val divResultNeg = opr1Neg ^ opr2Neg
  val remResultNeg = opr1Neg
  val opr1         = Mux(opr1Neg, -io.opr1, io.opr1)
  val opr2         = Mux(opr2Neg, -io.opr2, io.opr2)
  // 进行无符号除法运算
  val divresult = opr1.asUInt / opr2.asUInt
  val remresult = opr1.asUInt % opr2.asUInt

  io.valid     := io.en && !io.flush
  io.divresult := Mux(divResultNeg, -divresult, divresult)
  io.remresult := Mux(remResultNeg, -remresult, remresult)
}
