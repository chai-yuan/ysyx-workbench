package core

import chisel3._
import chisel3.util._

import bundle._

class DebugBundle extends Bundle {
  val pc     = Output(UInt(32.W))
  val decode = new DecodeDebugBundle()
}

class CPUTop extends Module {
  val io = IO(new Bundle {
    val instSRAM = new SRAMBundle()
    val dataSRAM = new SRAMBundle()
    val debug    = new DebugBundle()
  })
  val fetch   = Module(new Fetch())
  val decode  = Module(new Decode())
  val execute = Module(new Execute())

  fetch.io.instSRAM <> io.instSRAM
  fetch.io.resultBundle <> execute.io.resultBundle

  decode.io.inst := fetch.io.inst
  decode.io.resultBundle <> execute.io.resultBundle

  execute.io.controlBundle <> decode.io.controlBundle
  execute.io.dataSRAM <> io.dataSRAM
  execute.io.regSrc1 := decode.io.regSrc1
  execute.io.regSrc2 := decode.io.regSrc2
  execute.io.imm     := decode.io.imm
  execute.io.pc      := fetch.io.pc

  // debug
  io.debug.pc := fetch.io.pc
  io.debug.decode <> decode.io.debug
}
