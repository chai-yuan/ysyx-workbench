package core

import chisel3._
import chisel3.util._
import tools._
import config.Configs._

class FetchIO extends Bundle {
  val instSRAM = new SRAMBundle()
  val pc       = Output(UInt(32.W))
  val inst     = Output(UInt(32.W))
}

class Fetch extends Module {
  val io = IO(new FetchIO())

  val pc = RegInit(UInt(32.W), START_ADDR.U)

  pc := pc + 4.U

  io.instSRAM.en    := true.B
  io.instSRAM.we    := "b1111".U
  io.instSRAM.addr  := pc
  io.instSRAM.wdata := 0.U

  io.pc   := pc
  io.inst := io.instSRAM.rdata
}
