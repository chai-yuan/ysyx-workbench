package core.muldiv

import chisel3._
import chisel3.util._
import io._
import config.CPUconfig._
import core.define.OperationDefine._

/**
  * 乘法器
  * 使用Booth 2-bit乘法器，需要16周期完成32位乘法
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
  // 获得将要添加的符号位
  val opr1Sign = io.opr1Sign & io.opr1(DATA_WIDTH - 1)
  val opr2Sign = io.opr2Sign & io.opr2(DATA_WIDTH - 1)
  // 进行有符号乘法运算
  val opr1                               = Reg(UInt((DATA_WIDTH * 2).W))
  val opr2                               = Reg(UInt((DATA_WIDTH + 3).W))
  val mulResutl                          = Reg(UInt((DATA_WIDTH * 2).W))
  val count                              = RegInit(0.U(6.W))
  val (sIdle :: sWorking :: sEnd :: Nil) = Enum(3)
  val state                              = RegInit(sIdle)

  switch(state) {
    is(sIdle) {
      when(io.en && !io.flush) {
        opr1      := Cat(Fill(DATA_WIDTH, opr1Sign), io.opr1) // 扩充到2倍位宽
        opr2      := Cat(Fill(2, opr2Sign), io.opr2, 0.U(1.W)) // 扩充3位
        mulResutl := 0.U
        count     := 0.U
        state     := sWorking
      }
    }
    is(sWorking) {
      when(count <= (DATA_WIDTH / 2).U && opr2 =/= 0.U && !io.flush) {
        mulResutl := mulResutl + MuxLookup(opr2(2, 0), 0.U)(
          Seq(
            ("b000".U) -> (0.U),
            ("b001".U) -> (opr1),
            ("b010".U) -> (opr1),
            ("b011".U) -> (opr1 << 1),
            ("b100".U) -> -(opr1 << 1),
            ("b101".U) -> -(opr1),
            ("b110".U) -> -(opr1),
            ("b111".U) -> (0.U)
          )
        )
        opr1  := opr1 << 2
        opr2  := opr2 >> 2
        count := count + 1.U
      }.otherwise {
        state := sEnd
      }
    }
    is(sEnd) {
      state := sIdle
    }
  }

  // 计算结果
  val result = mulResutl

  io.valid  := io.en && !io.flush && state === sEnd
  io.result := result
}
