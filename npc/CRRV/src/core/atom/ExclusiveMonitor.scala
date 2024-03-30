package core.atom

import chisel3._
import chisel3.util._
import core.define.MemoryControlDefine._
import config.CPUconfig._
import io._

class ExclusiveMonitor extends Module {
  val io = IO(new Bundle {
    val flush  = Input(Bool())
    val check  = Flipped(new ExcMonCheckIO)
    val update = Input(new ExcMonCommitIO)
  })

  val flag = RegInit(false.B)
  val addr = RegInit(0.U(ADDR_WIDTH.W))

  when(io.flush || (io.update.clear && io.update.addr === addr)) {
    flag := false.B
    addr := 0.U
  }.elsewhen(io.update.set) {
    flag := true.B
    addr := io.update.addr
  }

  io.check.valid := flag && addr === io.check.addr
}
