package core

import chisel3._
import chisel3.util._

import config.Configs._

class RegistersIO extends Bundle {
  val regWriteEnable = Input(Bool())
  val regSrc1Idx     = Input(UInt(5.W))
  val regSrc2Idx     = Input(UInt(5.W))
  val regWriteIdx    = Input(UInt(5.W))
  val dataWrite      = Input(UInt(32.W))
  val dataRead1      = Output(UInt(32.W))
  val dataRead2      = Output(UInt(32.W))
}

class Registers extends Module {
  val io = IO(new RegistersIO())

  val regs = Reg(Vec(32, UInt(32.W)))
  // TODO
  // val regs = RegInit(Vec(32, UInt(32.W)), 0.U)

  io.dataRead1 := regs(io.regSrc1Idx)
  io.dataRead2 := regs(io.regSrc2Idx)

  when(io.regWriteEnable && io.regWriteIdx =/= 0.U) {
    regs(io.regWriteIdx) := io.dataWrite
  }
}
