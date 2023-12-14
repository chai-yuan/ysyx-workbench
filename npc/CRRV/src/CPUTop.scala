package core

import chisel3._
import chisel3.util._
import config.CPUconfig._
import io._
import memory.SimpleRAM
import memory.AXIliteRAM
import bus.Simple2AXIlite
import bus.SimpleArbiter

class CPUTop extends Module {
  val io = IO(new Bundle {
    val debug = new DebugIO
  })

  val core = Module(new Core)
  val arbiter = Module(new SimpleArbiter)
//   val simple2axi = Module(new Simple2AXIlite)
//   val ram = Module(new AXIliteRAM(false))
  val simpleram = Module(new SimpleRAM)

  core.io.inst <> arbiter.io.simpleInst
  core.io.data <> arbiter.io.simpleData
  
  arbiter.io.simpleOut <> simpleram.io
  // debug
  core.io.debug <> io.debug
}
