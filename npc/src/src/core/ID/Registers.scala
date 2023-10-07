package core.ID

import chisel3._
import chisel3.util._

class Registers extends Module {
  val io = IO(new Bundle {
    val wen    = Input(Bool())
    val waddr  = Input(UInt(5.W))
    val wdata  = Input(UInt(32.W))
    val raddr1 = Input(UInt(5.W))
    val raddr2 = Input(UInt(5.W))
    val rdata1 = Output(UInt(32.W))
    val rdata2 = Output(UInt(32.W))

    val debug = Output(Vec(32, UInt(32.W)))
  })

  val regfile = Mem(32, UInt(32.W))

  when(io.wen) {
    regfile(io.waddr) := io.wdata
  }

  // 如果正在写入的等同于将要读取的，进行前递
  io.rdata1 := Mux(io.raddr1.orR, Mux((io.raddr1 === io.waddr) && io.wen, io.wdata, regfile(io.raddr1)), 0.U)
  io.rdata2 := Mux(io.raddr2.orR, Mux((io.raddr2 === io.waddr) && io.wen, io.wdata, regfile(io.raddr2)), 0.U)

  for (i <- 0 until 32) {
    io.debug(i) := regfile(i.U)
  }
}
