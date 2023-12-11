package memory

import chisel3._
import chisel3.util._
import io.SimpleMemIO
import config.CPUconfig._
import sim.DPIC_RAM

class SimpleRAM extends Module {
  val io = IO(Flipped(new SimpleMemIO(ADDR_WIDTH, DATA_WIDTH)))

  val simram = Module(new DPIC_RAM)
  simram.io.clock := clock
  simram.io.reset := reset

  simram.io.ren   := io.enable && !io.wen.orR 
  simram.io.raddr := io.addr
  io.rdata        := RegNext(simram.io.rdata)

  simram.io.wen   := io.enable
  simram.io.wmask := io.wen
  simram.io.waddr := io.addr
  simram.io.wdata := io.wdata

  io.valid := true.B    // 永远有效
}
