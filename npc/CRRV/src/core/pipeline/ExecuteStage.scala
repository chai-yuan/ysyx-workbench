package core.pipeline

import chisel3._
import chisel3.util._
import config.CPUconfig._
import core.define.InstructionDefine._
import core.define.OperationDefine._
import core.define.ControlSignDefine._
import io._
import core.muldiv.MDU

class ExecuteStage extends Module {
  val io = IO(new Bundle {
    val id2exe  = Input(new ID2EXEIO)
    val control = new ExecuteStageControlIO
    val exe2mem = Output(new EXE2MEMIO)

    val csrRead    = new CsrReadIO
    val regForward = Output(new RegForwardIO)
  })
  val if2exe = io.id2exe.IF
  val id2exe = io.id2exe.ID

  val src1  = id2exe.src1
  val src2  = id2exe.src2
  val shamt = src2(4, 0)
  // ALU
  val aluResult = MuxLookup(id2exe.aluOp, 0.U)(
    Seq(
      ALU_NOP -> 0.U,
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
  // MDU
  val mdu = Module(new MDU)
  mdu.io.op    := id2exe.mduOp
  mdu.io.flush := io.control.flush
  mdu.io.opr1  := src1
  mdu.io.opr2  := src2
  val mduResult = mdu.io.result
  // CSR
  val csrResult = io.csrRead.data
  // 生成EXE段结果
  val result = MuxCase(
    0.U,
    Seq(
      (id2exe.aluOp =/= ALU_NOP) -> (aluResult),
      (id2exe.mduOp =/= MDU_NOP) -> (mduResult),
      (id2exe.csrOp =/= CSR_NOP) -> (csrResult)
    )
  )
  val load = id2exe.lsuOp =/= LSU_NOP && id2exe.regWen

  // 流水线控制
  io.control.stallReq := !mdu.io.valid
  // CSR 读取
  io.csrRead.op   := io.id2exe.ID.csrOp
  io.csrRead.addr := io.id2exe.ID.csrAddr
  // 前递操作
  io.regForward.en   := id2exe.regWen
  io.regForward.addr := id2exe.regWaddr
  io.regForward.data := result
  io.regForward.load := load
  // 下一级流水线
  io.exe2mem.IF <> if2exe
  io.exe2mem.ID <> id2exe
  io.exe2mem.EXE.load      := load
  io.exe2mem.EXE.exeResult := result
}

class ExecuteStageControlIO extends Bundle {
  val flush = Input(Bool())

  val stallReq = Output(Bool())
}
