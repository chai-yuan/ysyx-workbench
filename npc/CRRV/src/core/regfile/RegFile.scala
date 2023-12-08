package core.regfile

import chisel3._
import chisel3.util._
import config.CPUconfig._
import io.RegReadIO
import io.RegWriteIO

class RegFile extends Module {
  val io = IO(new Bundle {
    val read1 = Flipped(new RegReadIO)
    val read2 = Flipped(new RegReadIO)
    val write = Flipped(new RegWriteIO)
    val debug = Output(Vec(32, UInt(32.W)))
  })
  val regfile = RegInit(VecInit(Seq.fill(REG_COUNT) { 0.U(DATA_WIDTH.W) }))

  io.read1.data := Mux(io.read1.addr =/= 0.U, regfile(io.read1.addr), 0.U)
  io.read2.data := Mux(io.read2.addr =/= 0.U, regfile(io.read2.addr), 0.U)
  when(io.write.en && io.write.addr =/= 0.U) {
    regfile(io.write.addr) := io.write.data
  }

  // debug
  io.debug(0) := 0.U
  for (i <- 1 until 32) {
    io.debug(i) := regfile(i.U)
  }
}
