package core.pipeline

import chisel3._
import chisel3.util._
import config.CPUconfig._
import core.define.InstructionDefine._
import core.define.OperationDefine._
import core.define.MemoryControlDefine._
import io._

class MemoryStage extends Module {
  val io = IO(new Bundle {
    val exe2mem = Input(new EXE2MEMIO)
    val control = new MemoryStageControlIO
    val mem2wb  = Output(new MEM2WBIO)

    val dataRam    = new SimpleMemIO(ADDR_WIDTH, DATA_WIDTH)
    val regForward = Output(new RegForwardIO)
  })
  val if2mem  = io.exe2mem.IF
  val id2mem  = io.exe2mem.ID
  val exe2mem = io.exe2mem.EXE

  val ((en: Bool) :: (wen: Bool) :: (load: Bool) :: width ::
    (signed: Bool) :: (setExcMon: Bool) :: (checkExcMon: Bool) ::
    amoOp :: (flushIc: Bool) :: (flushDc: Bool) :: (flushIt: Bool) ::
    (flushDt: Bool) :: Nil) = ListLookup(id2mem.lsuOp, DEFAULT, TABLE)

  // 写数据
  val addr = exe2mem.aluResult
  val data = id2mem.lsuData
  val writeEn = MuxLookup(width, 0.U)(
    Seq(
      LS_DATA_BYTE -> ("b0001".U(4.W) << addr(1, 0)),
      LS_DATA_HALF -> ("b0011".U(4.W) << addr(1, 0)),
      LS_DATA_WORD -> "b1111".U(4.W)
    )
  )
  val writeData = MuxLookup(width, 0.U) {
    Seq(
      LS_DATA_BYTE -> Cat(data(7, 0), data(7, 0), data(7, 0), data(7, 0)),
      LS_DATA_HALF -> Cat(data(15, 0), data(15, 0)),
      LS_DATA_WORD -> data
    )
  }
  // 暂停信号
  val memStall = !io.dataRam.valid

  // 流水线控制
  io.control.stallReq := memStall
  io.control.flushReq := false.B
  io.control.flushPc  := 0.U
  // 访问内存
  io.dataRam.enable := en
  io.dataRam.wen    := writeEn
  io.dataRam.addr   := Cat(addr(DATA_WIDTH - 1, 2), 0.U(2.W))
  io.dataRam.wdata  := writeData
  // 前递操作
  io.regForward.en   := id2mem.regWen
  io.regForward.addr := id2mem.regWaddr
  io.regForward.data := exe2mem.aluResult
  io.regForward.load := load
  // 下一级流水线
  io.mem2wb.IF <> if2mem
  io.mem2wb.ID <> id2mem
  io.mem2wb.EXE <> exe2mem
  io.mem2wb.MEM.memRead     := load
  io.mem2wb.MEM.memSign     := signed
  io.mem2wb.MEM.memReadAddr := addr
  io.mem2wb.MEM.memReadLen  := width
}

class MemoryStageControlIO extends Bundle {
  val flush = Input(Bool())

  val stallReq = Output(Bool())
  val flushReq = Output(Bool())
  val flushPc  = Output(UInt(ADDR_WIDTH.W))
}