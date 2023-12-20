package core.muldiv

import chisel3._
import chisel3.util._
import io._
import config.CPUconfig._
import core.define.OperationDefine._

/**
  * 乘法器
  * 目前全部使用*运算符进行实现，之后再升级
  */
class Multiplier extends Module {
  val io = IO(new Bundle {
    val en    = Input(Bool())
    val flush = Input(Bool())
    val valid = Output(Bool())

    val opr1Sign = Input(Bool())
    val opr1     = Input(UInt(DATA_WIDTH.W))
    val opr2Sign = Input(Bool())
    val opr2     = Input(UInt(DATA_WIDTH.W))
    val result   = Output(UInt((DATA_WIDTH * 2).W))
  })
  // 改造输入操作数为有符号数
  val opr1 = Mux(io.opr1Sign, Cat(io.opr1(DATA_WIDTH - 1), io.opr1), Cat(0.U(1.W), io.opr1))
  val opr2 = Mux(io.opr2Sign, Cat(io.opr2(DATA_WIDTH - 1), io.opr2), Cat(0.U(1.W), io.opr2))
  // 计算结果
  val result = opr1.asSInt * opr2.asSInt

  io.valid  := io.en && !io.flush
  io.result := result(DATA_WIDTH * 2 - 1, 0)
}
