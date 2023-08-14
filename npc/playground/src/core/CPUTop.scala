package core

import chisel3._
import chisel3.util._

import tools.SRAMInterface

class CPUTop extends Module {
  val io = IO(new Bundle {
    val instSRAM = new SRAMInterface
    val dataSRAM = new SRAMInterface
  })

  io.instSRAM.en    := false.B
  io.instSRAM.wen   := 0.U
  io.instSRAM.addr  := 0.U
  io.instSRAM.wdata := 0.U

  io.dataSRAM.en    := false.B
  io.dataSRAM.wen   := 0.U
  io.dataSRAM.addr  := 0.U
  io.dataSRAM.wdata := 0.U

}
