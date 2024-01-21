package bus

import chisel3._
import chisel3.util._
import config.CPUconfig._
import io._

/**
  * 目前仅支持mtime，因而特别简单
  *
  * @param tickCount
  */
class CLINT(val tickCount: Int = 10) extends Module {
  val io = IO(Flipped(new SimpleIO(ADDR_WIDTH, DATA_WIDTH)))

  val mtime = RegInit(0.U(64.W))
  val tick  = RegInit(0.U(16.W))
  tick := tick + 1.U
  when(tick === tickCount.U) {
    tick  := 0.U
    mtime := mtime + 1.U
  }

  val outputData = RegInit(0.U(32.W))
  outputData := MuxCase(
    0.U,
    Seq(
      (io.out.bits.addr(15, 0) === 0.U) -> (mtime(31, 0)),
      (io.out.bits.addr(15, 0) === 4.U) -> (mtime(63, 32))
    )
  )

  io.out.ready := true.B
  io.in.rdata  := outputData
}
