package core

import chisel3._
import chisel3.util._
import config.CPUconfig._
import io._
import memory.SimpleRAM
import memory.AXIliteRAM
import bus.Simple2AXIlite
import bus.SimpleArbiter
import bus.AXIliteXbar

class CPUTop extends Module {
  val io = IO(new Bundle {
    val debug = new DebugIO
  })

  val core       = Module(new Core)
  val arbiter    = Module(new SimpleArbiter)
  val simple2axi = Module(new Simple2AXIlite)
  val axixbar    = Module(new AXIliteXbar)

  val ram  = Module(new AXIliteRAM(false))
  val ram2 = Module(new AXIliteRAM(false))
  core.io.inst <> arbiter.io.simpleInst
  core.io.data <> arbiter.io.simpleData

  arbiter.io.simpleOut <> simple2axi.io.simple
  simple2axi.io.axilite <> axixbar.io.in
  axixbar.io.ram <> ram.io
  axixbar.io.serial <> ram2.io
  // debug
  core.io.debug <> io.debug
}
