package core

import chisel3._
import chisel3.util._
import config.CPUconfig._
import io._
import memory.SimpleRAM

class CPUTop extends Module {
  val io = IO(new Bundle {
    val debug = new DebugIO
  })

  val core = Module(new Core)
  val inst = Module(new SimpleRAM)
  val data = Module(new SimpleRAM)

  core.io.inst <> inst.io
  core.io.data <> data.io
  core.io.debug <> io.debug
}
