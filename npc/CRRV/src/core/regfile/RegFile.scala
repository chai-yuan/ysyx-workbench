package core.regfile

import chisel3._
import chisel3.util._
import config.CPUconfig._
import io.RegReadIO
import io.RegWriteIO
import io.RegDebugIO

class RegFile extends Module {
  val io = IO(new Bundle {
    val read1 = Flipped(new RegReadIO)
    val read2 = Flipped(new RegReadIO)
    val write = Flipped(new RegWriteIO)

    val debug = Output(new RegDebugIO)
  })
  val regfile = Mem(32, UInt(DATA_WIDTH.W))

  io.read1.data := Mux(io.read1.addr =/= 0.U && io.read1.en, regfile(io.read1.addr), 0.U)
  io.read2.data := Mux(io.read2.addr =/= 0.U && io.read2.en, regfile(io.read2.addr), 0.U)
  when(io.write.en && io.write.addr =/= 0.U) {
    regfile(io.write.addr) := io.write.data
  }

  for (i <- 0 until 32) {
    io.debug.regs(i) := regfile(i)
  }
}
