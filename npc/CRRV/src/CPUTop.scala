package core

import chisel3._
import chisel3.util._
import config.CPUconfig._
import io._
import memory.SimpleRAM
import memory.AXIliteRAM
import bus.Simple2AXIlite

class CPUTop extends Module {
  val io = IO(new Bundle {
    val debug = new DebugIO
  })

//   val core = Module(new Core)
//   val sinst2axiinst = Module(new Simple2AXIlite)
//   val sdata2axidata = Module(new Simple2AXIlite)
//   val axiInst = Module(new AXIliteRAM)
//   val axiData = Module(new AXIliteRAM)

//   core.io.inst <> sinst2axiinst.io.simple
//   core.io.data <> sdata2axidata.io.simple
//   core.io.debug <> io.debug

//   sinst2axiinst.io.axilite <> axiInst.io
//   sdata2axidata.io.axilite <> axiData.io

  val core = Module(new Core)
  val inst = Module(new SimpleRAM)
  val data = Module(new SimpleRAM)

  core.io.inst <> inst.io
  core.io.data <> data.io
  core.io.debug <> io.debug
}
