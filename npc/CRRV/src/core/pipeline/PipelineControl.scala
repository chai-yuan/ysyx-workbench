package core.pipeline

import chisel3._
import chisel3.util._
import config.CPUconfig._
import core.define.OperationDefine._
import core.regfile.CsrInfoIO

/**
  * 流水线控制模块
  * 接受来自各个模块的信号，生成各级流水线的暂停和冲刷信号
  */
class PipelineControl extends Module {
  val io = IO(new Bundle {
    val ifStallReq  = Input(Bool())
    val exeStallReq = Input(Bool())
    val memStallReq = Input(Bool())

    val idFlushReq     = Input(Bool())
    val idFlushTarget  = Input(UInt(ADDR_WIDTH.W))
    val memFlushReq    = Input(Bool())
    val memFlushTarget = Input(UInt(ADDR_WIDTH.W))

    val loadHazardFlage = Input(Bool())
    val csrHazardFlag   = Input(Bool())

    val exceptType = Input(UInt(EXC_TYPE_WIDTH.W))
    val csrInfo    = Flipped(new CsrInfoIO)

    val stallIF  = Output(Bool())
    val stallID  = Output(Bool())
    val stallEXE = Output(Bool())
    val stallMEM = Output(Bool())
    val stallWB  = Output(Bool())

    val flushAll = Output(Bool())
    val flushIF  = Output(Bool())
    val flushPc  = Output(UInt(ADDR_WIDTH.W))
  })
  val stall = MuxCase(
    0.U,
    Seq(
      (io.memStallReq) -> "b11110".U(5.W),
      (io.exeStallReq || io.csrHazardFlag) -> "b11100".U(5.W),
      (io.loadHazardFlage) -> "b11000".U(5.W),
      (io.ifStallReq) -> "b10000".U(5.W)
    )
  )
  // 异常
  val excFlush = io.exceptType =/= EXC_NONE
  val excPc = MuxLookup(io.exceptType, 0.U)(
    Seq(
      (EXC_ECALL) -> (io.csrInfo.trapEnterVec),
      (EXC_MRET) -> (io.csrInfo.mepc)
    )
  )
  // 生成清空信号
  val flushAll = io.memFlushReq || excFlush || io.csrInfo.intr
  val flushIF  = flushAll || io.idFlushReq
  val flushPc = MuxCase(
    0.U,
    Seq(
      (io.csrInfo.intr) -> (io.csrInfo.trapEnterVec),
      (excFlush) -> (excPc),
      (io.memFlushReq) -> (io.memFlushTarget),
      (io.idFlushReq) -> (io.idFlushTarget)
    )
  )

  io.stallIF  := stall(4)
  io.stallID  := stall(3)
  io.stallEXE := stall(2)
  io.stallMEM := stall(1)
  io.stallWB  := stall(0)

  io.flushAll := flushAll
  io.flushIF  := flushIF
  io.flushPc  := flushPc
}
