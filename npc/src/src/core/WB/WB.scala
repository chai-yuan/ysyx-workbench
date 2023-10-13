package core.WB

import chisel3._
import chisel3.util._
import config.Config
import core.MEM.MEM2WBBundle
import config.WriteBackOp

class WB2IDBundle extends Bundle {
  val regwen   = Output(Bool())
  val regaddr  = Output(UInt(5.W))
  val regwdata = Output(UInt(32.W))
}

class WBBundle extends Bundle {
  val wb2id = new WB2IDBundle

  val mem2wb   = Flipped(new MEM2WBBundle)
  val readData = Input(UInt(32.W))
  // debug
  val debugPc = Output(UInt(32.W))
}

class WB extends Module {
  val io = IO(new WBBundle)

  val control = io.mem2wb.control
  // wb2id
  io.wb2id.regwen  := control.wbEn
  io.wb2id.regaddr := io.mem2wb.inst(11, 7)
  io.wb2id.regwdata := MuxCase(
    io.mem2wb.aluResult,
    Seq(
      (control.wbOp === WriteBackOp.WB_ALU) -> io.mem2wb.aluResult,
      (control.wbOp === WriteBackOp.WB_MEM) -> io.readData
    )
  )

  // debug
  io.debugPc := io.mem2wb.pc
}
