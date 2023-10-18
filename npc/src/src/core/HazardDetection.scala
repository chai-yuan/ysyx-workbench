package core

import chisel3._
import chisel3.util._
import config.Config
import core.ID.ID2HazerdBundle
import config.Inst._

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

  val inst = io.id2hazerd.inst
  val imm  = io.id2hazerd.imm
  val reg1 = io.id2hazerd.regData1
  val reg2 = io.id2hazerd.regData2
  val pc   = io.id2hazerd.pc

  val nextPC = Lookup(
    inst,
    pc + 4.U,
    Seq(
      // Jump
      JAL -> (pc.asUInt + imm.asUInt),
      JALR -> (reg1.asUInt + imm.asUInt),
      // Branch
      BEQ -> Mux(
        reg1.asUInt === reg2.asUInt,
        pc.asUInt + imm.asUInt,
        pc.asUInt + 4.U
      ),
      BNE -> Mux(
        reg1.asUInt =/= reg2.asUInt,
        pc.asUInt + imm.asUInt,
        pc.asUInt + 4.U
      ),
      BLT -> Mux(
        reg1.asSInt < reg2.asSInt,
        pc.asUInt + imm.asUInt,
        pc.asUInt + 4.U
      ),
      BGE -> Mux(
        reg1.asSInt >= reg2.asSInt,
        pc.asUInt + imm.asUInt,
        pc.asUInt + 4.U
      ),
      BLTU -> Mux(
        reg1.asUInt < reg2.asUInt,
        pc.asUInt + imm.asUInt,
        pc.asUInt + 4.U
      ),
      (BGEU -> Mux(
        reg1.asUInt >= reg2.asUInt,
        pc.asUInt + imm.asUInt,
        pc.asUInt + 4.U
      ))
      // ---
    )
  )
  val nextPCSel = !(nextPC === pc + 4.U)

  val halt = (inst(6, 0) === "b1110011".U && imm === 1.U)

  io.hazard2if.ifFlush   := nextPCSel
  io.hazard2if.ifStop    := halt
  io.hazard2if.nextPC    := nextPC
  io.hazard2if.nextPCSel := nextPCSel

  io.hazerd2id.idFlush   := false.B
  io.hazerd2exe.exeFlush := false.B
  io.hazerd2mem.memFlush := false.B
}
