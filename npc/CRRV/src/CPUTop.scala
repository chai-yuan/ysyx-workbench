package core

import chisel3._
import chisel3.util._
import config.CPUconfig._
import io._

class CPUTop extends Module {
  val io = IO(new Bundle {
    val inst  = new SimpleMemIO(ADDR_WIDTH, INST_WIDTH)
    val data  = new SimpleMemIO(ADDR_WIDTH, DATA_WIDTH)
    val debug = new DebugIO
  })

  val core = Module(new Core)

  core.io.inst <> io.inst
  core.io.data <> io.data
  core.io.debug <> io.debug
}
