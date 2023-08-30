package core

import chisel3._
import bundle._

class Execute extends Module {
  val io = IO(new Bundle {
    val regSrc1       = Input(UInt(32.W))
    val regSrc2       = Input(UInt(32.W))
    val imm           = Input(UInt(32.W))
    val pc            = Input(UInt(32.W))
    val controlBundle = Flipped(new ControlBundle())
    val resultBundle  = new ResultBundle()
    val dataSRAM      = new SRAMBundle()
  })

  io.resultBundle.nextPC       := io.pc + 4.U
  io.resultBundle.regDataWrite := io.regSrc1 + io.imm

  io.dataSRAM.en    := false.B
  io.dataSRAM.we    := "b1111".U
  io.dataSRAM.addr  := 0.U
  io.dataSRAM.wdata := 0.U
}
