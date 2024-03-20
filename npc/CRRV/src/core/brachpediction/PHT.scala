package core.brachpediction

import chisel3._
import config.CPUconfig._
import core.define.OperationDefine._
import core.define.CsrDefine._
import chisel3.util.Cat
import chisel3.util.log2Ceil

class PHT(val GHRWidth: Int) extends Module {
  val io = IO(new Bundle {
    val lastBranch = Input(Bool())
    val lastTaken  = Input(Bool())
    val lastIndex  = Input(UInt(GHRWidth.W))

    val index = Input(UInt(GHRWidth.W))
    val taken = Output(Bool())
  })
  val init     = Seq.fill((1 << GHRWidth)) { "b10".U(2.W) }
  val counters = RegInit(VecInit(init))

  when(io.lastBranch) {
    when(counters(io.lastIndex) === "b11".U) {
      when(!io.lastTaken) {
        counters(io.lastIndex) := counters(io.lastIndex) - 1.U
      }
    }.elsewhen(counters(io.lastIndex) === "b00".U) {
      when(io.lastTaken) {
        counters(io.lastIndex) := counters(io.lastIndex) + 1.U
      }
    }.otherwise {
      when(!io.lastTaken) {
        counters(io.lastIndex) := counters(io.lastIndex) - 1.U
      }.otherwise {
        counters(io.lastIndex) := counters(io.lastIndex) + 1.U
      }
    }
  }

  io.taken := counters(io.index)(1)
}
