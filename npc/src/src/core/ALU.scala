package core

import chisel3._
import chisel3.util._
import bundle._
import config.OPType._

class ALUIO extends Bundle {
  val src1          = Input(UInt(32.W))
  val src2          = Input(UInt(32.W))
  val pc            = Input(UInt(32.W))
  val controlBundle = Flipped(new ControlBundle())
  val result        = Output(UInt(32.W))
  val branchResult  = Output(Bool())
}

class ALU extends Module {
  val io = IO(new ALUIO())

  val result       = WireDefault(0.U(32.W))
  val branchResult = WireDefault(false.B)

  switch(io.controlBundle.ALUop) {
    is(OP_NOP) {
      result       := 0.U
      branchResult := false.B
    }
    is(OP_ADD) {
      branchResult := io.src1 +& io.src2
    }
    is(OP_SUB) {
      branchResult := io.src1 -& io.src2
    }
    is(OP_AND) {
      branchResult := io.src1 & io.src2
    }
    is(OP_OR) {
      branchResult := io.src1 | io.src2
    }
    is(OP_XOR) {
      branchResult := io.src1 ^ io.src2
    }
    is(OP_SLL) {
      branchResult := io.src1 << io.src2(4, 0)
    }
    is(OP_SRL) {
      branchResult := io.src1 >> io.src2(4, 0)
    }
    is(OP_SRA) {
      branchResult := (io.src1.asSInt >> io.src2(4, 0)).asUInt
    }
    is(OP_EQ) {
      branchResult := io.src1.asSInt === io.src2.asSInt
    }
    is(OP_NEQ) {
      branchResult := io.src1.asSInt =/= io.src2.asSInt
    }
    is(OP_LT) {
      when(io.controlBundle.branch) {
        when(io.controlBundle.ALUunsigned) {
          branchResult := io.src1.asSInt < io.src2.asSInt
        }.otherwise {
          branchResult := io.src1 < io.src2
        }
      }.otherwise {
        when(io.controlBundle.ALUunsigned) {
          result := io.src1.asSInt < io.src2.asSInt
        }.otherwise {
          result := io.src1 < io.src2
        }
      }
    }
    is(OP_GE) {
      when(io.controlBundle.ALUunsigned) {
        branchResult := io.src1.asSInt >= io.src2.asSInt
      }.otherwise {
        branchResult := io.src1 >= io.src2
      }
    }
  }

  io.result := MuxCase(
    result,
    Array(
      (io.controlBundle.jal || io.controlBundle.jalr) -> (io.pc + io.src2),
      (io.controlBundle.lui) -> (io.src2),
      (io.controlBundle.auipc) -> (io.pc + io.src2)
    )
  )
  io.branchResult := branchResult
}
