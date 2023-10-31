package memory

import chisel3._
import chisel3.util._

class SRAMBundle extends Bundle {
  val ren   = Input(Bool())
  val raddr = Input(UInt(32.W))
  val rdata = Output(UInt(32.W))

  val wen   = Input(Bool())
  val waddr = Input(UInt(32.W))
  val wdata = Input(UInt(32.W))
  val wmask = Input(UInt(4.W))
}

class SRAM extends Module {
  val io = IO(new Bundle {
    val ren   = Input(Bool())
    val raddr = Input(UInt(32.W))
    val rdata = Output(UInt(32.W))

    val wen   = Input(Bool())
    val waddr = Input(UInt(32.W))
    val wdata = Input(UInt(32.W))
    val wmask = Input(UInt(4.W))
  })

  // 使用dpic接入到仿真器
  val dpicRAM = Module(new DPIC_RAM)

  dpicRAM.io.ren   := io.ren
  dpicRAM.io.raddr := io.raddr
  io.rdata         := RegNext(dpicRAM.io.rdata)

  dpicRAM.io.wen   := io.wen
  dpicRAM.io.waddr := io.waddr
  dpicRAM.io.wdata := io.wdata
  dpicRAM.io.wmask := io.wmask

}
