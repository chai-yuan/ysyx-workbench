package core

import chisel3._
import chisel3.util._
import config.CPUconfig._
import io._
import bus.Simple2AXIlite
import bus.SimpleArbiter
import sim.Debug

class CRRVTop extends Module {
  val io = IO(new Bundle {
    val axi = new AXIliteMasterIO(ADDR_WIDTH, DATA_WIDTH)
  })
  val core           = Module(new Core)
  val arbiter        = Module(new SimpleArbiter)
  val simple2axilite = Module(new Simple2AXIlite)
  val debug          = Module(new Debug)

  core.io.inst <> arbiter.io.simpleInst
  core.io.data <> arbiter.io.simpleData

  arbiter.io.simpleOut <> simple2axilite.io.simple
  simple2axilite.io.axilite <> io.axi

  debug.io.clock := clock
  debug.io.reset := reset
  core.io.debug <> debug.io.debug
}
