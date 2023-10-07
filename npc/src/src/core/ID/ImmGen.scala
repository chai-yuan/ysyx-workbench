package core.ID

import chisel3._
import chisel3.util._
import config.InstType

class ImmGenBundle extends Bundle {
  val inst     = Input(UInt(32.W))
  val instType = Input(UInt(InstType.InstTypeWidth))
  val imm      = Output(UInt(32.W))
}

class ImmGen extends Module {
  val io = IO(new ImmGenBundle)

  val immI = io.inst(31, 20).asUInt
  val immS = Cat(
    Seq(
      io.inst(31, 25).asUInt,
      io.inst(11, 7).asUInt
    )
  )
  val immB = Cat(
    Seq(
      io.inst(31, 31).asUInt,
      io.inst(7, 7).asUInt,
      io.inst(30, 25).asUInt,
      io.inst(11, 8).asUInt,
      Fill(1, 0.U(1.W)).asUInt
    )
  )
  val immU = Cat(
    Seq(
      io.inst(31, 12).asUInt,
      Fill(12, 0.U(1.W)).asUInt
    )
  )
  val immJ = Cat(
    Seq(
      io.inst(31, 31).asUInt,
      io.inst(19, 12).asUInt,
      io.inst(20, 20).asUInt,
      io.inst(30, 21).asUInt,
      Fill(1, 0.U(1.W)).asUInt
    )
  )

  io.imm := MuxCase(
    0.U(32.W),
    Seq(
      (io.instType === InstType.instI) -> Cat(Fill(20, immI(11)), immI),
      (io.instType === InstType.instS) -> Cat(Fill(20, immS(11)), immS),
      (io.instType === InstType.instB) -> Cat(Fill(19, immB(12)), immB),
      (io.instType === InstType.instU) -> immU,
      (io.instType === InstType.instJ) -> Cat(Fill(11, immJ(20)), immJ)
    )
  )
}
