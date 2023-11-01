package memory

import chisel3._
import chisel3.util._
import config.GenConfig

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

  if (GenConfig.simMemoryDPIC) {
    // 使用dpic接入到仿真器
    val dpicRAM = Module(new DPIC_RAM)
    dpicRAM.io.clock := clock
    dpicRAM.io.reset := reset

    dpicRAM.io.ren   := io.ren
    dpicRAM.io.raddr := io.raddr
    io.rdata         := RegNext(dpicRAM.io.rdata)

    dpicRAM.io.wen   := io.wen
    dpicRAM.io.waddr := io.waddr
    dpicRAM.io.wdata := io.wdata
    dpicRAM.io.wmask := io.wmask
  } else {
    val regRAM = Module(new REG_RAM)
    regRAM.io.ren   := io.ren
    regRAM.io.raddr := io.raddr
    io.rdata        := RegNext(regRAM.io.rdata)

    regRAM.io.wen   := io.wen
    regRAM.io.waddr := io.waddr
    regRAM.io.wdata := io.wdata
    regRAM.io.wmask := io.wmask
  }

}
