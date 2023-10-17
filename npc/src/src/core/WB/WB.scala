package core.WB

import chisel3._
import chisel3.util._
import config.Config
import core.MEM.MEM2WBBundle
import config.WriteBackOp
import core.ID.ControlBundle

class WB2IDBundle extends Bundle {
  val regwen   = Output(Bool())
  val regaddr  = Output(UInt(5.W))
  val regwdata = Output(UInt(32.W))
}

class WB2ForwardBundle extends Bundle {
  val enable = Output(Bool())
  val addr   = Output(UInt(32.W))
  val data   = Output(UInt(32.W))
}

class WBBundle extends Bundle {
  val wb2id      = new WB2IDBundle
  val wb2forward = new WB2ForwardBundle

  val mem2wb   = Flipped(new MEM2WBBundle)
  val readData = Input(UInt(32.W))
  // debug
  val debugInst = Output(UInt(32.W))
  val debugHalt = Output(Bool())
  val debugPC   = Output(UInt(32.W))
}

class WB extends Module {
  val io = IO(new WBBundle)

  val control = io.mem2wb.control
  // wb2id
  io.wb2id.regwen  := control.wbEn
  io.wb2id.regaddr := io.mem2wb.inst(11, 7)
  val regwdata = MuxCase(
    io.mem2wb.aluResult,
    Seq(
      (control.wbOp === WriteBackOp.WB_ALU) -> io.mem2wb.aluResult,
      (control.wbOp === WriteBackOp.WB_MEM) -> io.readData
    )
  )
  io.wb2id.regwdata := regwdata
  // wb2forward
  io.wb2forward.enable := control.wbEn
  io.wb2forward.addr   := io.mem2wb.inst(11, 7)
  io.wb2forward.data   := regwdata
  // debug
  io.debugInst := io.mem2wb.inst
  io.debugHalt := io.mem2wb.halt
  io.debugPC   := io.mem2wb.pc
}
