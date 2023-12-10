package core.pipeline

import chisel3._
import chisel3.util._
import io._

/**
  * 冒险处理单元
  * 用于处理寄存器和CSR的前递，以及在冲突时向控制模块发送暂停信号
  */
class HazardResolver extends Module {
  val io = IO(new Bundle {
    val regRead1 = Flipped(new RegReadIO)
    val regRead2 = Flipped(new RegReadIO)

    val exeForward = Input(new RegForwardIO)
    val memForward = Input(new RegForwardIO)
    val wbForward  = Input(new RegForwardIO)

    val regFile1 = new RegReadIO
    val regFile2 = new RegReadIO

    val loadHazardFlag = Output(Bool())
  })
  // 前递操作
  def forwardReg(read: RegReadIO, rf: RegReadIO) = {
    when(read.en && read.addr =/= 0.U) {
      when(io.exeForward.en && read.addr === io.exeForward.addr) {
        read.data := io.exeForward.data
      }.elsewhen(io.memForward.en && read.addr === io.memForward.addr) {
        read.data := io.memForward.data
      }.elsewhen(io.wbForward.en && read.addr === io.wbForward.addr) {
        read.data := io.wbForward.data
      }.otherwise {
        read.data := rf.data
      }
    }.otherwise {
      read.data := 0.U
    }
  }
  forwardReg(io.regRead1, io.regFile1)
  forwardReg(io.regRead2, io.regFile2)

  // 访存暂停
  def resolveLoadHazard(read: RegReadIO) = {
    val aluLoad = io.exeForward.load && read.addr === io.exeForward.addr
    val memLoad = io.memForward.load && read.addr === io.memForward.addr
    read.en && (aluLoad || memLoad)
  }
  val loadHazard1 = resolveLoadHazard(io.regRead1)
  val loadHazard2 = resolveLoadHazard(io.regRead2)

  io.regFile1.en   := io.regRead1.en
  io.regFile1.addr := io.regRead1.addr
  io.regFile2.en   := io.regRead2.en
  io.regFile2.addr := io.regRead2.addr

  io.loadHazardFlag := loadHazard1 || loadHazard2
}
