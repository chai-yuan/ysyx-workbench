package core

import chisel3._
import chisel3.util._
import debug._

class MemBundle extends Bundle {
  val addr      = Output(UInt(32.W))
  val writeEn   = Output(Bool())
  val writeData = Output(UInt(32.W))
  val readEn    = Output(Bool())
  val readData  = Input(UInt(32.W))
  val mark      = Output(UInt(4.W))
}

class CPUTop extends Module {
  val io = IO(new Bundle {
    val inst  = new MemBundle
    val data  = new MemBundle
    val debug = new DebugBundle
  })

}
