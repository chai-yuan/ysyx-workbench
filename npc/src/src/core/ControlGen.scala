package core

import chisel3._
import bundle._

import config.InstType._
import config.OPType._
import config.InstType

class ControlGen extends Module {
  val io = IO(new Bundle {
    val op            = Input(UInt(7.W))
    val funct3        = Input(UInt(3.W))
    val funct7        = Input(UInt(7.W))
    val controlBundle = new ControlBundle()
  })

  val instType       = Mux(io.op === "b0110011".U, Inst_R, Inst_I)
  val regWriteEnable = (instType === Inst_I) | (instType === Inst_R)
  val ALUop          = Mux(io.funct3 === "b000".U, OP_ADD, OP_SUB)
  val ALUsrc2imm     = io.op === "b0010011".U

  io.controlBundle.instType       := instType
  io.controlBundle.regWriteEnable := regWriteEnable
  io.controlBundle.ALUop          := ALUop
  io.controlBundle.ALUsrc2imm     := ALUsrc2imm
}
