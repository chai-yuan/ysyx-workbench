package core.EXE

import chisel3._
import chisel3.util._
import config.AluOp

class ALUBundle extends Bundle {
  val aluOp = Input(UInt(AluOp.AluOpWidth))
  val src1  = Input(UInt(32.W))
  val src2  = Input(UInt(32.W))
  val out   = Output(UInt(32.W))
}

class ALU extends Module {
  val io = IO(new ALUBundle)

  io.out := MuxCase(
    0.U(32.W),
    Seq(
      (io.aluOp === AluOp.ALU_NOP) -> (io.src1).asUInt,
      (io.aluOp === AluOp.ALU_ADD) -> (io.src1 + io.src2).asUInt,
      (io.aluOp === AluOp.ALU_SLT) -> Mux(io.src1.asSInt < io.src2.asSInt, 1.U(32.W), 0.U(32.W)).asUInt,
      (io.aluOp === AluOp.ALU_SLTU) -> Mux(io.src1.asUInt < io.src2.asUInt, 1.U(32.W), 0.U(32.W)).asUInt,
      (io.aluOp === AluOp.ALU_XOR) -> (io.src1 ^ io.src2).asUInt,
      (io.aluOp === AluOp.ALU_OR) -> (io.src1 | io.src2).asUInt,
      (io.aluOp === AluOp.ALU_AND) -> (io.src1 & io.src2).asUInt,
      (io.aluOp === AluOp.ALU_SLL) -> (io.src1.asUInt << io.src2(4, 0)).asUInt,
      (io.aluOp === AluOp.ALU_SRL) -> (io.src1.asUInt >> io.src2(4, 0)).asUInt,
      (io.aluOp === AluOp.ALU_SRA) -> (io.src1.asSInt >> io.src2(4, 0)).asUInt,
      (io.aluOp === AluOp.ALU_SUB) -> (io.src1 - io.src2).asUInt
    )
  )

}
