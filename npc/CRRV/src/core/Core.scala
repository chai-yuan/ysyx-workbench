package core

import chisel3._
import chisel3.util._
import io.SimpleMemIO
import config.CPUconfig._
import io.DebugIO

class Core extends Module {
  val io = IO(new Bundle {
    val inst  = new SimpleMemIO(ADDR_WIDTH, INST_WIDTH)
    val data  = new SimpleMemIO(ADDR_WIDTH, DATA_WIDTH)
    val debug = new DebugIO
  })



  
}
