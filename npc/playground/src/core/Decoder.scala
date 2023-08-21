package core

import chisel3._
import chisel3.util._
import tools._
import config.OPType._

class DecoderIO extends Bundle {
  val inst          = Input(UInt(32.W))
  val controlBundle = new ControlBundle()
  val regDataWrite  = Input(UInt(32.W))
  val regSrc1       = Output(UInt(32.W))
  val regSrc2       = Output(UInt(32.W))
  val imm           = Output(UInt(32.W))
}

class Decoder extends Module {
  val io = IO(new DecoderIO())

  val registers  = Module(new Registers())
  val immGen     = Module(new ImmGen())
  val controlGen = Module(new ControlGen())

  val opcode = io.inst(6, 0)
  val funct3 = io.inst(14, 12)
  val funct7 = io.inst(31, 25)
  controlGen.io.op     := opcode
  controlGen.io.funct3 := funct3
  controlGen.io.funct7 := funct7
  io.controlBundle <> controlGen.io.controlBundle

  immGen.io.instType := controlGen.io.controlBundle.instType
  immGen.io.inst     := io.inst
  io.imm             := immGen.io.imm

  registers.io.regWriteEnable := controlGen.io.controlBundle.regWriteEnable
  registers.io.regSrc1Idx     := io.inst(19, 15)
  registers.io.regSrc2Idx     := io.inst(24, 20)
  registers.io.regWriteIdx    := io.inst(11, 7)
  registers.io.dataWrite      := io.regDataWrite
  io.regSrc1                  := registers.io.dataRead1
  io.regSrc2                  := registers.io.dataRead2
}
