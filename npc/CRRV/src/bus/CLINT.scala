package bus

import chisel3._
import chisel3.util._
import config.CPUconfig._
import io._

/**
  * CLINT 支持计时和时钟中断
  *
  * @param tickCount
  */
class CLINT(val tickCount: Int = 16) extends Module {
  val io = IO(new Bundle {
    val core = Flipped(new SimpleIO(ADDR_WIDTH, DATA_WIDTH))
    val intr = Output(Bool())
  })
  val core = io.core
  val addr = core.out.bits.addr

  val mtime       = RegInit(0.U(64.W))
  val timermatchl = RegInit(0.U(32.W))
  val timermatchh = RegInit(0.U(32.W))
  val timermatch  = Cat(timermatchh, timermatchl)
  val tick        = RegInit(0.U(8.W))

  tick := tick + 1.U
  when(tick === tickCount.U) {
    tick  := 0.U
    mtime := mtime + 1.U
  }

  val writeEn = core.out.valid && core.out.bits.writeEn
  timermatchl := Mux(addr(15, 0) === "h4000".U && writeEn, core.out.bits.wdata, timermatchl)
  timermatchh := Mux(addr(15, 0) === "h4004".U && writeEn, core.out.bits.wdata, timermatchh)

  val outputData = RegInit(0.U(32.W))
  outputData := MuxCase(
    0.U,
    Seq(
      (addr(15, 0) === "hbff8".U) -> (mtime(31, 0)),
      (addr(15, 0) === "hbffc".U) -> (mtime(63, 32))
    )
  )

  core.out.ready := true.B
  core.in.rdata  := outputData
  io.intr        := (timermatch =/= 0.U) && (mtime >= timermatch)
}
