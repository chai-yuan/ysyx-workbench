package core.ID

import chisel3._
import chisel3.util._
import core.WB._

class Forward extends Module {
  val io = IO(new Bundle {
    val exeForward = Flipped(new WriteBackBundle)
    val memForward = Flipped(new WriteBackBundle)
    val writeBack  = Flipped(new WriteBackBundle)

    val addr1 = Input(UInt(5.W))
    val data1 = Output(UInt(32.W))
    val addr2 = Input(UInt(5.W))
    val data2 = Output(UInt(32.W))

    val debugRegs = Output(Vec(32, UInt(32.W)))
  })

  val regs = Module(new Registers)
  regs.io.raddr1 := io.addr1
  regs.io.raddr2 := io.addr2
  regs.io.wen    := io.writeBack.enable
  regs.io.waddr  := io.writeBack.wAddr
  regs.io.wdata  := io.writeBack.wData

  io.data1 := MuxCase(
    regs.io.rdata1,
    Seq(
      (io.exeForward.enable && (io.exeForward.wAddr === io.addr1)) -> (io.exeForward.wData),
      (io.memForward.enable && (io.memForward.wAddr === io.addr1)) -> (io.memForward.wData)
    )
  )
  io.data2 := MuxCase(
    regs.io.rdata2,
    Seq(
      (io.exeForward.enable && (io.exeForward.wAddr === io.addr2)) -> (io.exeForward.wData),
      (io.memForward.enable && (io.memForward.wAddr === io.addr2)) -> (io.memForward.wData)
    )
  )

  io.debugRegs := regs.io.debug
}
