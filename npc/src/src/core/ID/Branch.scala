package core.ID

import chisel3._
import chisel3.util._
import config.Inst._

class Branch extends Module {
  val io = IO(new Bundle {
    val inst     = Input(UInt(32.W))
    val regData1 = Input(UInt(32.W))
    val regData2 = Input(UInt(32.W))
    val imm      = Input(UInt(32.W))
    val pc       = Input(UInt(32.W))
    val csrData  = Input(UInt(32.W))

    val nextPCsel = Output(Bool())
    val nextPC    = Output(UInt(32.W))
  })

  val inst    = io.inst
  val imm     = io.imm
  val reg1    = io.regData1
  val reg2    = io.regData2
  val pc      = io.pc
  val csrData = io.csrData

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
      BGEU -> Mux(
        reg1.asUInt >= reg2.asUInt,
        pc.asUInt + imm.asUInt,
        pc.asUInt + 4.U
      ),
      // ---
      ECALL -> csrData,
      MRET -> csrData
    )
  )

  io.nextPC    := nextPC
  io.nextPCsel := !(nextPC === pc + 4.U)
}
