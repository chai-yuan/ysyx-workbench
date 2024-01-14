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

    val dataRam     = Decoupled(new SimpleOutIO(ADDR_WIDTH, DATA_WIDTH))
    val memCsrStall = Output(new Csr2HazardResolverIO)
    val regForward  = Output(new RegForwardIO)
  })
  val if2mem  = io.exe2mem.IF
  val id2mem  = io.exe2mem.ID
  val exe2mem = io.exe2mem.EXE

  val ((en: Bool) :: (wen: Bool) :: width ::
    (signed: Bool) :: (setExcMon: Bool) :: (checkExcMon: Bool) ::
    amoOp :: (flushIc: Bool) :: (flushDc: Bool) :: (flushIt: Bool) ::
    (flushDt: Bool) :: Nil) = ListLookup(id2mem.lsuOp, DEFAULT, TABLE)

  // 写数据
  val addr = exe2mem.exeResult
  val data = id2mem.lsuData
  val writeData = MuxLookup(width, 0.U) {
    Seq(
      LS_DATA_BYTE -> Cat(data(7, 0), data(7, 0), data(7, 0), data(7, 0)),
      LS_DATA_HALF -> Cat(data(15, 0), data(15, 0)),
      LS_DATA_WORD -> data
    )
  }
  // 暂停信号
  val memStall = (!io.dataRam.ready) && en
  // 流水线控制
  io.control.stallReq := memStall
  io.control.flushReq := false.B
  io.control.flushPc  := 0.U
  // 访问内存
  io.dataRam.valid        := en
  io.dataRam.bits.writeEn := wen
  io.dataRam.bits.size    := width
  io.dataRam.bits.addr    := addr
  io.dataRam.bits.wdata   := writeData
  // CSR
  io.memCsrStall.op   := id2mem.csrOp
  io.memCsrStall.addr := id2mem.csrAddr
  // 前递操作
  io.regForward.en   := id2mem.regWen
  io.regForward.addr := id2mem.regWaddr
  io.regForward.data := exe2mem.exeResult
  io.regForward.load := exe2mem.load
  // 下一级流水线
  io.mem2wb.IF <> if2mem
  io.mem2wb.ID <> id2mem
  io.mem2wb.EXE <> exe2mem
  io.mem2wb.MEM.memAddr := addr
}

class MemoryStageControlIO extends Bundle {
  val flush = Input(Bool())

  val stallReq = Output(Bool())
  val flushReq = Output(Bool())
  val flushPc  = Output(UInt(ADDR_WIDTH.W))
}
