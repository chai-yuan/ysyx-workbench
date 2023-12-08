package core.pipeline

import chisel3._
import chisel3.util._
import io.PipelineStageIO

/**
  * 通过流水线控制信号，管理流水线之间的信号传递
  *
  * @param sio
  */
class PipelineStage[T <: PipelineStageIO](sio: T) extends Module {
  val io = IO(new Bundle {
    // 流水线控制信号
    val flush     = Input(Bool())
    val stallPrev = Input(Bool())
    val stallNext = Input(Bool())
    // 连接前后两级的流水线
    val prev = Input(sio)
    val next = Output(sio)
  })

  val pipelineReg = RegInit(sio, sio.default())
  when(io.flush || (io.stallPrev && !io.stallNext)) {
    pipelineReg := sio.default()
  }.elsewhen(!io.stallPrev) {
    pipelineReg := io.prev
  }

  io.next := pipelineReg
}
