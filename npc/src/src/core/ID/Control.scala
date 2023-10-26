package core.ID

import chisel3._
import chisel3.util._
import config.InstType
import config._
import config.Inst._
import org.apache.commons.lang3.concurrent.EventCountCircuitBreaker

class ControlBundle extends Bundle {
  val instType = Output(UInt(InstType.InstTypeWidth))

  val src1Op     = Output(UInt(AluSrcOp.AluSrcOpWidth))
  val src2Op     = Output(UInt(AluSrcOp.AluSrcOpWidth))
  val aluOp      = Output(UInt(AluOp.AluOpWidth))
  val memOp      = Output(UInt(MemOp.MemOpWidth))
  val memReadEn  = Output(Bool())
  val memWriteEn = Output(Bool())
  val wbOp       = Output(UInt(WriteBackOp.WriteBackOpWidth))

  val halt = Output(Bool())
}

class Control extends Module {
  val io = IO(new Bundle {
    val inst       = Input(UInt(32.W))
    val outControl = new ControlBundle
  })

  val opcode = io.inst(6, 0)
  val funct3 = io.inst(14, 12)
  val funct7 = io.inst(31, 25)
  val inst   = io.inst

  io.outControl.src1Op := MuxCase(
    AluSrcOp.SrcReg,
    Seq(
      (inst === AUIPC) -> (AluSrcOp.SrcPC),
      (inst === JAL || inst === JALR) -> (AluSrcOp.SrcSeqPC),
      (inst === LUI) -> (AluSrcOp.SrcImm)
    )
  )
  io.outControl.src2Op := MuxCase(
    AluSrcOp.SrcReg,
    Seq(
      (opcode === "b0010011".U) -> (AluSrcOp.SrcImm),
      (opcode === "b0000011".U || opcode === "b0100011".U) -> (AluSrcOp.SrcImm),
      (inst === AUIPC) -> (AluSrcOp.SrcImm)
    )
  )

  io.outControl.aluOp := Lookup(
    io.inst,
    AluOp.ALU_NOP,
    Seq(
      LUI -> AluOp.ALU_PSV,
      CSRRW -> AluOp.ALU_PSV,
      CSRRS -> AluOp.ALU_PSV,
      JAL -> AluOp.ALU_PSV,
      JALR -> AluOp.ALU_PSV,
      AUIPC -> AluOp.ALU_ADD,
      SB -> AluOp.ALU_ADD,
      SH -> AluOp.ALU_ADD,
      SW -> AluOp.ALU_ADD,
      ADD -> AluOp.ALU_ADD,
      ADDI -> AluOp.ALU_ADD,
      LB -> AluOp.ALU_ADD,
      LBU -> AluOp.ALU_ADD,
      LH -> AluOp.ALU_ADD,
      LHU -> AluOp.ALU_ADD,
      LW -> AluOp.ALU_ADD,
      SUB -> AluOp.ALU_SUB,
      AND -> AluOp.ALU_AND,
      ANDI -> AluOp.ALU_AND,
      OR -> AluOp.ALU_OR,
      ORI -> AluOp.ALU_OR,
      XOR -> AluOp.ALU_XOR,
      XORI -> AluOp.ALU_XOR,
      SLT -> AluOp.ALU_SLT,
      SLTI -> AluOp.ALU_SLT,
      SLTU -> AluOp.ALU_SLTU,
      SLTIU -> AluOp.ALU_SLTU,
      SLL -> AluOp.ALU_SLL,
      SLLI -> AluOp.ALU_SLL,
      SRL -> AluOp.ALU_SRL,
      SRLI -> AluOp.ALU_SRL,
      SRA -> AluOp.ALU_SRA,
      SRAI -> AluOp.ALU_SRA
    )
  )

  io.outControl.memOp := Lookup(
    io.inst,
    MemOp.MEM_NOP,
    Seq(
      LB -> MemOp.MEM_B,
      LBU -> MemOp.MEM_BU,
      LH -> MemOp.MEM_H,
      LHU -> MemOp.MEM_HU,
      LW -> MemOp.MEM_W,
      SB -> MemOp.MEM_B,
      SH -> MemOp.MEM_H,
      SW -> MemOp.MEM_W
    )
  )

  io.outControl.memReadEn  := (opcode === "b0000011".U)
  io.outControl.memWriteEn := (opcode === "b0100011".U)

  io.outControl.wbOp := MuxCase(
    WriteBackOp.WB_NOP,
    Seq(
      (opcode === "b0000011".U) -> (WriteBackOp.WB_MEM),
      (opcode === "b0010011".U || opcode === "b0110011".U) -> (WriteBackOp.WB_ALU),
      (inst === JAL || inst === JALR || inst === LUI || inst === AUIPC) -> (WriteBackOp.WB_ALU)
    )
  )

  io.outControl.instType := MuxCase(
    InstType.instR,
    Seq(
      (opcode === "b0110011".U) -> (InstType.instR),
      (opcode === "b0010011".U || opcode === "b0000011".U || opcode === "b1100111".U || opcode === "b1110011".U) -> (InstType.instI),
      (opcode === "b0100011".U) -> (InstType.instS),
      (opcode === "b1100011".U) -> (InstType.instB),
      (opcode === "b1101111".U) -> (InstType.instJ),
      (opcode === "b0110111".U || opcode === "b0010111".U) -> (InstType.instU)
    )
  )

  io.outControl.halt := inst === EBREAK
}
