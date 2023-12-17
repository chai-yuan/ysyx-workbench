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
import device.AXIliteSerial
import device.AXIliteCLINT

class CPUTop extends Module {
  val io = IO(new Bundle {
    val debug = new DebugIO
  })

  val core       = Module(new Core)
  val arbiter    = Module(new SimpleArbiter)
  val simple2axi = Module(new Simple2AXIlite)
  val axixbar    = Module(new AXIliteXbar)

  core.io.inst <> arbiter.io.simpleInst
  core.io.data <> arbiter.io.simpleData
  core.io.debug <> io.debug

  arbiter.io.simpleOut <> simple2axi.io.simple
  simple2axi.io.axilite <> axixbar.io.in

  val ram    = Module(new AXIliteRAM(false))
  val serial = Module(new AXIliteSerial)
  val clint  = Module(new AXIliteCLINT)

  axixbar.io.ram <> ram.io
  axixbar.io.serial <> serial.io
  axixbar.io.clint <> clint.io
}
