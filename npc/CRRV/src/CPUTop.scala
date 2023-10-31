package core

import chisel3._
import chisel3.util._
import debug.DebugBundle
import memory.SRAM

class CPUTop extends Module {
  val io = IO(new Bundle {
    val debug = new DebugBundle
  })

  val core    = Module(new CoreTop)
  val instRAM = Module(new SRAM)
  val dataRAM = Module(new SRAM)

  core.io.inst <> instRAM.io
  core.io.data <> dataRAM.io

  core.io.debug <> io.debug
}
