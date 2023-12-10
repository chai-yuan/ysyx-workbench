package core.pipeline

import chisel3._
import chisel3.util._
import config.CPUconfig._
import core.define.InstructionDefine._
import core.define.OperationDefine._
import core.define.ControlSignDefine._
import io._

class ExecuteStage extends Module {
  val io = IO(new Bundle {
    val id2exe  = Input(new ID2EXEIO)
    val control = new ExecuteStageControlIO
    val exe2mem = Output(new EXE2MEMIO)

    val regForward = Output(new RegForwardIO)
  })
  val if2exe = io.id2exe.IF
  val id2exe = io.id2exe.ID

  val src1  = id2exe.src1
  val src2  = id2exe.src2
  val shamt = src2(4, 0)

  val aluResult = MuxLookup(id2exe.aluOp, 0.U)(
    Seq(
      ALU_ADD -> (src1 + src2),
      ALU_SUB -> (src1 - src2),
      ALU_XOR -> (src1 ^ src2),
      ALU_OR -> (src1 | src2),
      ALU_AND -> (src1 & src2),
      ALU_SLT -> (src1.asSInt < src2.asSInt).asUInt,
      ALU_SLTU -> (src1 < src2),
      ALU_SLL -> (src1 << shamt),
      ALU_SRL -> (src1 >> shamt),
      ALU_SRA -> (src1.asSInt >> shamt).asUInt
    )
  )
  val mduResult = 0.U // TODO:之后再支持乘除法

  val result = aluResult
  val load   = id2exe.lsuOp =/= LSU_NOP && id2exe.regWen

  // 流水线控制
  io.control.stallReq := false.B
  // 前递操作
  io.regForward.en   := id2exe.regWen
  io.regForward.addr := id2exe.regWaddr
  io.regForward.data := result
  io.regForward.load := load
  // 下一级流水线
  io.exe2mem.IF <> if2exe
  io.exe2mem.ID <> id2exe
  io.exe2mem.EXE.load      := load
  io.exe2mem.EXE.aluResult := result
}

class ExecuteStageControlIO extends Bundle {
  val flush = Input(Bool())

  val stallReq = Output(Bool())
}
