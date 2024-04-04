package core.muldiv

import chisel3._
import chisel3.util._
import io._
import config.CPUconfig._
import core.define.OperationDefine._

/**
  * 除法器
  * 使用迭代除法完成运算(TODO: 除零和溢出是否需要额外考虑?)
  */
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
  // 检测除零和溢出
  val divideZero = (io.opr2 === 0.U)
  val overflow   = (io.opr1 === "h8000_0000".U && io.opr2 === "hffff_ffff".U)
  // 记录结果的符号状态
  val opr1Neg      = io.sign && io.opr1(DATA_WIDTH - 1)
  val opr2Neg      = io.sign && io.opr2(DATA_WIDTH - 1)
  val divResultNeg = opr1Neg ^ opr2Neg
  val remResultNeg = opr1Neg
  val opr1         = Mux(opr1Neg, -io.opr1, io.opr1)
  val opr2         = Mux(opr2Neg, -io.opr2, io.opr2)
  // 进行无符号除法运算
  val dividend                           = Reg(UInt((DATA_WIDTH * 2).W))
  val divisor                            = Reg(UInt((DATA_WIDTH + 1).W))
  val quotient                           = Reg(UInt(DATA_WIDTH.W))
  val count                              = RegInit(0.U(6.W))
  val (sIdle :: sWorking :: sEnd :: Nil) = Enum(3)
  val state                              = RegInit(sIdle)

  switch(state) {
    is(sIdle) {
      when(io.en && !io.flush) {
        dividend := Cat(0.U(32.W), opr1)
        divisor  := Cat(0.U(1.W), opr2)
        quotient := 0.U
        count    := 0.U
        state    := sWorking
      }
    }
    is(sWorking) {
      when(count =/= DATA_WIDTH.U && !io.flush) {
        val ans = dividend(DATA_WIDTH * 2 - 1, DATA_WIDTH - 1) - divisor
        quotient := Cat(quotient(DATA_WIDTH - 2, 0), !ans(DATA_WIDTH))
        dividend := Mux(ans(DATA_WIDTH), dividend << 1, Cat(ans, dividend(DATA_WIDTH - 2, 0)) << 1)
        count    := count + 1.U
      }.otherwise {
        state := sEnd
      }
    }
    is(sEnd) {
      state := sIdle
    }
  }
  val divresult = quotient
  val remresult = dividend(DATA_WIDTH * 2 - 1, DATA_WIDTH)
  // 输出结果
  io.valid := io.en && !io.flush && state === sEnd
  io.divresult := MuxCase(
    divresult,
    Seq(
      (divideZero) -> ("hffff_ffff".U),
      (io.sign && overflow) -> (io.opr1),
      (divResultNeg) -> (-divresult)
    )
  )
  io.remresult := MuxCase(
    remresult,
    Seq(
      (divideZero) -> (io.opr1),
      (io.sign && overflow) -> (0.U),
      (remResultNeg) -> (-remresult)
    )
  )
}
