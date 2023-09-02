package core

import chisel3._
import chisel3.util._
import bundle._
import config.Configs._

class DecodeIO extends Bundle {
  val inst          = Input(UInt(32.W))
  val controlBundle = new ControlBundle()
  val regSrc1       = Output(UInt(32.W))
  val regSrc2       = Output(UInt(32.W))
  val imm           = Output(UInt(32.W))
  val resultBundle  = Flipped(new ResultBundle())

  val debug = new DecodeDebugBundle()
}

class DecodeDebugBundle extends Bundle {
  val regs   = Output(Vec(32, UInt(32.W)))
  val ebreak = Output(Bool())
}

class Decode extends Module {
  val io = IO(new DecodeIO())

  val registers  = Module(new Registers())
  val immGen     = Module(new ImmGen())
  val controlGen = Module(new ControlGen())

  val opcode = io.inst(6, 0)
  val funct3 = io.inst(14, 12)
  val funct7 = io.inst(31, 25)
  controlGen.io.opcode := opcode
  controlGen.io.funct3 := funct3
  controlGen.io.funct7 := funct7
  io.controlBundle <> controlGen.io.controlBundle

  immGen.io.opcode := opcode
  immGen.io.inst   := io.inst
  io.imm           := immGen.io.imm

  registers.io.regWriteEnable := controlGen.io.controlBundle.regWriteEnable
  registers.io.regSrc1Idx     := io.inst(19, 15)
  registers.io.regSrc2Idx     := io.inst(24, 20)
  registers.io.regWriteIdx    := io.inst(11, 7)
  registers.io.dataWrite      := io.resultBundle.regDataWrite
  io.regSrc1                  := registers.io.dataRead1
  io.regSrc2                  := registers.io.dataRead2

  // debug
  io.debug.ebreak := (opcode === "b1110011".U)
  io.debug.regs   := registers.io.debug
}
