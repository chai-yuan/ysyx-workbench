package core.WB

import chisel3._
import chisel3.util._
import config.Config
import core.MEM.MEM2WBBundle

class WB2IDBundle extends Bundle {
  val regwen   = Output(Bool())
  val regaddr  = Output(UInt(5.W))
  val regwdata = Output(UInt(32.W))
}

class WBBundle extends Bundle {
  val mem2wb = Flipped(new MEM2WBBundle)
  val wb2id  = new WB2IDBundle
}

class WB extends Module {
  val io = IO(new WBBundle)

  // wb2id
  io.wb2id.regaddr  := io.mem2wb.aluResult
  io.wb2id.regwdata := io.mem2wb.aluResult
  // io.wb2id.regwen   := io.mem2wb.control
}
