package core

import chisel3._
import config.InstType._
import chisel3.util._

class ImmGen extends Module {
  val io = IO(new Bundle {
    val opcode = Input(UInt(7.W))
    val inst   = Input(UInt(32.W))
    val imm    = Output(UInt(32.W))
  })

  val I_type = (io.opcode === "b0010011".U) || (io.opcode === "b0000011".U) || (io.opcode === "b1110011".U)
  val S_type = (io.opcode === "b0100011".U)
  val B_type = (io.opcode === "b1100011".U)
  val U_type = (io.opcode === "b0110111".U) || (io.opcode === "b0010111".U)
  val J_type = (io.opcode === "b1101111".U)

  val immI = Cat(Fill(20, io.inst(31)), io.inst(31, 20))
  val immS = Cat(Fill(20, io.inst(31)), io.inst(31, 25), io.inst(11, 7))
  val immB = Cat(Fill(20, io.inst(31)), io.inst(7), io.inst(30, 25), io.inst(11, 8), 0.U(1.W))
  val immU = Cat(io.inst(31, 12), Fill(12, 0.U))
  val immJ = Cat(Fill(12, io.inst(31)), io.inst(31), io.inst(19, 12), io.inst(20), io.inst(30, 21), Fill(1, 0.U))

  io.imm := MuxCase(
    0.U,
    Array(
      I_type -> immI,
      S_type -> immS,
      B_type -> immB,
      U_type -> immU,
      J_type -> immJ
    )
  )

}
