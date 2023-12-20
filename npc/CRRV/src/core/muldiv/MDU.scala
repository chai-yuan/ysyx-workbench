package core.muldiv

import chisel3._
import chisel3.util._
import io._
import config.CPUconfig._
import core.define.OperationDefine._

/**
  * 乘除法器
  * 用于支持riscv的M拓展
  */
class MDU extends Module {
  val io = IO(new Bundle {
    val op    = Input(UInt(MDU_OP_WIDTH.W))
    val flush = Input(Bool())
    val valid = Output(Bool())

    val opr1   = Input(UInt(DATA_WIDTH.W))
    val opr2   = Input(UInt(DATA_WIDTH.W))
    val result = Output(UInt(DATA_WIDTH.W))
  })

  val mulEnable :: divEnable :: hight :: remainder :: leftSign :: rightSign :: Nil = ListLookup(
    io.op,
    MduDecode.DEFAULT,
    MduDecode.TABLE
  )

  // 乘法
  val multiplier = Module(new Multiplier)
  multiplier.io.en       := mulEnable
  multiplier.io.flush    := io.flush
  multiplier.io.opr1Sign := leftSign
  multiplier.io.opr1     := io.opr1
  multiplier.io.opr2Sign := rightSign
  multiplier.io.opr2     := io.opr2
  val mulResult =
    Mux(hight, multiplier.io.result(DATA_WIDTH * 2 - 1, DATA_WIDTH), multiplier.io.result(DATA_WIDTH - 1, 0))
  // 除法
  val divider = Module(new Divider)
  divider.io.en    := divEnable
  divider.io.flush := io.flush
  divider.io.sign  := leftSign && rightSign
  divider.io.opr1  := io.opr1
  divider.io.opr2  := io.opr2
  val divResult = Mux(remainder, divider.io.remresult, divider.io.divresult)

  io.valid := MuxCase(
    true.B,
    Seq(
      (mulEnable) -> (multiplier.io.valid),
      (divEnable) -> (divider.io.valid)
    )
  )
  io.result := MuxCase(
    0.U,
    Seq(
      (mulEnable) -> (mulResult),
      (divEnable) -> (divResult)
    )
  )
}

object MduDecode {
  val Y = true.B
  val N = false.B

  val DEFAULT = List(N, N, N, N, N, N)
  val TABLE = Array(
    BitPat(MDU_MUL) -> List(Y, N, N, N, N, N),
    BitPat(MDU_MULH) -> List(Y, N, Y, N, Y, Y),
    BitPat(MDU_MULHSU) -> List(Y, N, Y, N, Y, N),
    BitPat(MDU_MULHU) -> List(Y, N, Y, N, N, N),
    BitPat(MDU_DIV) -> List(N, Y, N, N, Y, Y),
    BitPat(MDU_DIVU) -> List(N, Y, N, N, N, N),
    BitPat(MDU_REM) -> List(N, Y, N, Y, Y, Y),
    BitPat(MDU_REMU) -> List(N, Y, N, Y, N, N)
  )
}
