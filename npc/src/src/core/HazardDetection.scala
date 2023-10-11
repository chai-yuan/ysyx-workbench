package core

import chisel3._
import chisel3.util._
import config.Config
import core.ID.ID2HazerdBundle

class Hazerd2IFBundle extends Bundle {
  val ifStop  = Output(Bool())
  val ifFlush = Output(Bool())

  val nextPCSel = Output(Bool())
  val nextPC    = Output(UInt(32.W))
}
class Hazerd2IDBundle extends Bundle {
  val idFlush = Output(Bool())
}
class Hazerd2EXEBundle extends Bundle {
  val exeFlush = Output(Bool())
}
class Hazerd2MEMBundle extends Bundle {
  val memFlush = Output(Bool())
}

class HazardDetection extends Module {
  val io = IO(new Bundle {
    val hazard2if  = new Hazerd2IFBundle
    val hazerd2id  = new Hazerd2IDBundle
    val hazerd2exe = new Hazerd2EXEBundle
    val hazerd2mem = new Hazerd2MEMBundle

    val id2hazerd = Flipped(new ID2HazerdBundle)
  })

  io.hazard2if.ifFlush   := false.B
  io.hazard2if.ifStop    := false.B
  io.hazard2if.nextPC    := 0.U
  io.hazard2if.nextPCSel := false.B

  io.hazerd2id.idFlush   := false.B
  io.hazerd2exe.exeFlush := false.B
  io.hazerd2mem.memFlush := false.B
}
