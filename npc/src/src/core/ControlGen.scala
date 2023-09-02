package core

import chisel3._
import bundle._

import config.InstType._
import config.OPType._
import config.InstType
import chisel3.util.MuxCase

class ControlGen extends Module {
  val io = IO(new Bundle {
    val opcode        = Input(UInt(7.W))
    val funct3        = Input(UInt(3.W))
    val funct7        = Input(UInt(7.W))
    val controlBundle = new ControlBundle()
  })

  val add  = (io.opcode === "b0110011".U) && (io.funct3 === "h0".U) && (io.funct7 === "h00".U)
  val sub  = (io.opcode === "b0110011".U) && (io.funct3 === "h0".U) && (io.funct7 === "h20".U)
  val xor  = (io.opcode === "b0110011".U) && (io.funct3 === "h4".U) && (io.funct7 === "h00".U)
  val or   = (io.opcode === "b0110011".U) && (io.funct3 === "h6".U) && (io.funct7 === "h00".U)
  val and  = (io.opcode === "b0110011".U) && (io.funct3 === "h7".U) && (io.funct7 === "h00".U)
  val sll  = (io.opcode === "b0110011".U) && (io.funct3 === "h1".U) && (io.funct7 === "h00".U)
  val srl  = (io.opcode === "b0110011".U) && (io.funct3 === "h5".U) && (io.funct7 === "h00".U)
  val sra  = (io.opcode === "b0110011".U) && (io.funct3 === "h5".U) && (io.funct7 === "h20".U)
  val slt  = (io.opcode === "b0110011".U) && (io.funct3 === "h2".U) && (io.funct7 === "h00".U)
  val sltu = (io.opcode === "b0110011".U) && (io.funct3 === "h3".U) && (io.funct7 === "h00".U)

  val addi  = (io.opcode === "b0010011".U) && (io.funct3 === "h0".U)
  val xori  = (io.opcode === "b0010011".U) && (io.funct3 === "h4".U)
  val ori   = (io.opcode === "b0010011".U) && (io.funct3 === "h6".U)
  val andi  = (io.opcode === "b0010011".U) && (io.funct3 === "h7".U)
  val slli  = (io.opcode === "b0010011".U) && (io.funct3 === "h1".U) && (io.funct7 === "h00".U)
  val srli  = (io.opcode === "b0010011".U) && (io.funct3 === "h5".U) && (io.funct7 === "h00".U)
  val srai  = (io.opcode === "b0010011".U) && (io.funct3 === "h5".U) && (io.funct7 === "h20".U)
  val slti  = (io.opcode === "b0010011".U) && (io.funct3 === "h2".U)
  val sltiu = (io.opcode === "b0010011".U) && (io.funct3 === "h3".U)

  val lb  = (io.opcode === "b0000011".U) && (io.funct3 === "h0".U)
  val lh  = (io.opcode === "b0000011".U) && (io.funct3 === "h1".U)
  val lw  = (io.opcode === "b0000011".U) && (io.funct3 === "h2".U)
  val lbu = (io.opcode === "b0000011".U) && (io.funct3 === "h4".U)
  val lhu = (io.opcode === "b0000011".U) && (io.funct3 === "h5".U)

  val sb = (io.opcode === "b0100011".U) && (io.funct3 === "h0".U)
  val sh = (io.opcode === "b0100011".U) && (io.funct3 === "h1".U)
  val sw = (io.opcode === "b0100011".U) && (io.funct3 === "h2".U)

  val beq  = (io.opcode === "b1100011".U) && (io.funct3 === "h0".U)
  val bne  = (io.opcode === "b1100011".U) && (io.funct3 === "h1".U)
  val blt  = (io.opcode === "b1100011".U) && (io.funct3 === "h4".U)
  val bge  = (io.opcode === "b1100011".U) && (io.funct3 === "h5".U)
  val bltu = (io.opcode === "b1100011".U) && (io.funct3 === "h6".U)
  val bgeu = (io.opcode === "b1100011".U) && (io.funct3 === "h7".U)

  val jal  = (io.opcode === "b1101111".U)
  val jalr = (io.opcode === "b1100111".U) && (io.funct3 === "h0".U)

  val lui   = (io.opcode === "b0110111".U)
  val auipc = (io.opcode === "b0010111".U)

  io.controlBundle.regWriteEnable := (io.opcode === "b0110011".U) ||
    (io.opcode === "b0010011".U) ||
    (io.opcode === "b0000011".U) ||
    (io.opcode === "b1101111".U) ||
    (io.opcode === "b1100111".U) ||
    (io.opcode === "b0110111".U) ||
    (io.opcode === "b0010111".U)

  io.controlBundle.ALUop := MuxCase(
    OP_NOP,
    Array(
      (add || addi || io.opcode === "b0000011".U || io.opcode === "b0100011".U) -> OP_ADD,
      (sub) -> OP_SUB,
      (and || andi) -> OP_AND,
      (or || ori) -> OP_OR,
      (xor || xori) -> OP_XOR,
      (sll || slli) -> OP_SLL,
      (srl || srli) -> OP_SRL,
      (sra || srai) -> OP_SRA,
      (beq) -> OP_EQ,
      (bne) -> OP_NEQ,
      (blt || bltu) -> OP_LT,
      (bge || bgeu) -> OP_GE
    )
  )

  io.controlBundle.ALUsrc2imm := (io.opcode === "b001001".U) ||
    (io.opcode === "b0000011".U) || (io.opcode === "b0100011".U)

  io.controlBundle.ALUunsigned := (sltu || sltiu || bltu || bgeu)

  io.controlBundle.branch := (io.opcode === "b1100011".U)

  io.controlBundle.mem2reg := (io.opcode === "b0000011".U)

  io.controlBundle.memWriteEnable := (io.opcode === "b0100011".U)

  io.controlBundle.memWe := MuxCase(
    0.U,
    Array(
      (lb || lbu || sb) -> "b0001".U,
      (lh || lhu || sh) -> "b0010".U,
      (lw || sw) -> "b0100".U
    )
  )

  io.controlBundle.jal   := jal
  io.controlBundle.jalr  := jalr
  io.controlBundle.lui   := lui
  io.controlBundle.auipc := auipc
}
