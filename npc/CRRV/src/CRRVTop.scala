package core

import chisel3._
import chisel3.util._
import config.CPUconfig._
import io._
import sim.Debug
import bus._

class CRRVTop extends Module {
  val io = IO(new Bundle {
    val interrupt = Input(Bool())
    val axi       = new AXI4MasterIO(ADDR_WIDTH, DATA_WIDTH)
  })
  val core       = Module(new Core)
  val arbiter    = Module(new SimpleArbiter)
  val simple2axi = Module(new Simple2AXI4)
  val debug      = Module(new Debug)

  core.io.inst <> arbiter.io.simpleInst
  core.io.data <> arbiter.io.simpleData

  arbiter.io.simpleOut <> simple2axi.io.simple
  simple2axi.io.axi <> io.axi

  debug.io.clock := clock
  debug.io.reset := reset
  core.io.debug <> debug.io.debug
}
