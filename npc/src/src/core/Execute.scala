package core

import chisel3._
import bundle._
import chisel3.util.MuxCase

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

  val alu = Module(new ALU())

  alu.io.src1 := io.regSrc1
  alu.io.src2 := Mux(io.controlBundle.ALUsrc2imm, io.imm, io.regSrc2)
  alu.io.pc   := io.pc
  alu.io.controlBundle <> io.controlBundle

  io.resultBundle.nextPC := MuxCase(
    io.pc + 4.U,
    Array(
      (io.controlBundle.jal || alu.io.branchResult || io.controlBundle.branch) -> (io.pc + io.imm),
      (io.controlBundle.jalr) -> (io.regSrc1 + io.imm)
    )
  )
  io.resultBundle.regDataWrite := Mux(io.controlBundle.mem2reg, io.dataSRAM.rdata, alu.io.result)

  io.dataSRAM.en    := io.controlBundle.memWriteEnable
  io.dataSRAM.we    := io.controlBundle.memWe
  io.dataSRAM.addr  := alu.io.result
  io.dataSRAM.wdata := io.regSrc2
}
