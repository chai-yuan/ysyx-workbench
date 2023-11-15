package core

import chisel3._
import chisel3.util._
import debug.DebugBundle
import memory.SRAM
import memory.AXIliteRAM

class CPUTop extends Module {
  val io = IO(new Bundle {
    val debug = new DebugBundle
  })

  val core    = Module(new CoreTop)
  val instRAM = Module(new AXIliteRAM)
  val dataRAM = Module(new AXIliteRAM)

  core.io.inst <> instRAM.io
  core.io.data <> dataRAM.io

  core.io.debug <> io.debug
}
