
import chisel3._
import chisel3.util._

import tools._

class CPUTop extends Module {
  val io = IO(new Bundle {
    val instSRAM = new SRAMBundle()

    val debug = new DebugBundle()
  })
  val fetch   = Module(new Fetch())
  val decode  = Module(new Decoder())
  val execute = Module(new Execute())

  fetch.io.instSRAM <> io.instSRAM

  decode.io.inst         := fetch.io.inst
  decode.io.pc           := fetch.io.pc
  decode.io.regDataWrite := execute.io.regDataWrite

  execute.io.controlBundle <> decode.io.controlBundle
  execute.io.regSrc1 := decode.io.regSrc1
  execute.io.regSrc2 := decode.io.regSrc2
  execute.io.imm     := decode.io.imm

  // debug
  io.debug <> decode.io.debug
}
