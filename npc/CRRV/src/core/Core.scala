package core

import chisel3._
import chisel3.util._
import config.CPUconfig._
import core.pipeline._
import core.regfile._
import io._
import core.atom.ExclusiveMonitor
import core.define.OperationDefine

class Core extends Module {
  val io = IO(new Bundle {
    val inst  = new SimpleIO(ADDR_WIDTH, INST_WIDTH)
    val data  = new SimpleIO(ADDR_WIDTH, DATA_WIDTH)
    val intr  = new InterruptIO
    val debug = Output(new DebugIO)
  })

  val fetchStage     = Module(new FetchStage)
  val if2id          = Module(new PipelineStage(new IF2IDIO))
  val decodeStage    = Module(new DecodeStage)
  val id2exe         = Module(new PipelineStage(new ID2EXEIO))
  val executeStage   = Module(new ExecuteStage)
  val exe2mem        = Module(new PipelineStage(new EXE2MEMIO))
  val memoryStage    = Module(new MemoryStage)
  val mem2wb         = Module(new PipelineStage(new MEM2WBIO))
  val writeBackStage = Module(new WriteBackStage)

  val pipelineControl = Module(new PipelineControl)
  val hazardResolver  = Module(new HazardResolver)
  val csrFile         = Module(new CsrFile)
  val regFile         = Module(new RegFile)
  val excMon          = Module(new ExclusiveMonitor)

  fetchStage.io.instRom <> io.inst.out
  fetchStage.io.control.flush   := pipelineControl.io.flushIF
  fetchStage.io.control.flushPC := pipelineControl.io.flushPc
  fetchStage.io.control.stall   := pipelineControl.io.stallIF
  fetchStage.io.branchInfo      := decodeStage.io.branchInfo
  if2id.io.flush                := pipelineControl.io.flushIF
  if2id.io.stallPrev            := pipelineControl.io.stallIF
  if2id.io.stallNext            := pipelineControl.io.stallID
  if2id.io.prev <> fetchStage.io.if2id

  decodeStage.io.if2id <> if2id.io.next
  decodeStage.io.read          := io.inst.in
  decodeStage.io.control.stall := pipelineControl.io.stallID
  decodeStage.io.regRead1 <> hazardResolver.io.regRead1
  decodeStage.io.regRead2 <> hazardResolver.io.regRead2
  id2exe.io.flush     := pipelineControl.io.flushAll
  id2exe.io.stallPrev := pipelineControl.io.stallID
  id2exe.io.stallNext := pipelineControl.io.stallEXE
  id2exe.io.prev <> decodeStage.io.id2exe

  executeStage.io.id2exe <> id2exe.io.next
  executeStage.io.control.flush := pipelineControl.io.flushAll
  executeStage.io.csrRead <> hazardResolver.io.csrRead
  exe2mem.io.flush     := pipelineControl.io.flushAll
  exe2mem.io.stallPrev := pipelineControl.io.stallEXE
  exe2mem.io.stallNext := pipelineControl.io.stallMEM
  exe2mem.io.prev <> executeStage.io.exe2mem

  memoryStage.io.exe2mem <> exe2mem.io.next
  memoryStage.io.control.flush := pipelineControl.io.flushAll
  memoryStage.io.dataRam <> io.data
  mem2wb.io.flush     := pipelineControl.io.flushAll
  mem2wb.io.stallPrev := pipelineControl.io.stallMEM
  mem2wb.io.stallNext := pipelineControl.io.stallWB
  mem2wb.io.prev <> memoryStage.io.mem2wb

  writeBackStage.io.mem2wb <> mem2wb.io.next
  writeBackStage.io.read := io.data.in

  // register file
  regFile.io.read1 <> hazardResolver.io.regFile1
  regFile.io.read2 <> hazardResolver.io.regFile2
  regFile.io.write.en   := writeBackStage.io.regForward.en
  regFile.io.write.addr := writeBackStage.io.regForward.addr
  regFile.io.write.data := writeBackStage.io.regForward.data

  val timer_intr =
    io.intr.timer && io.debug.debugInfo.valid && writeBackStage.io.wb2csr.exceptType === OperationDefine.EXC_NONE
  // csr file
  csrFile.io.read <> hazardResolver.io.regCsr
  csrFile.io.write <> writeBackStage.io.wb2csr
  csrFile.io.intr := timer_intr

  // excMon
  excMon.io.flush <> pipelineControl.io.flushAll
  excMon.io.check <> hazardResolver.io.excMonCheck
  excMon.io.update.addr  := writeBackStage.io.excMonCommit.addr
  excMon.io.update.set   := writeBackStage.io.excMonCommit.set
  excMon.io.update.clear := writeBackStage.io.excMonCommit.clear

  // hazard resolver
  hazardResolver.io.exeForward <> executeStage.io.regForward
  hazardResolver.io.memForward <> memoryStage.io.regForward
  hazardResolver.io.wbForward <> writeBackStage.io.regForward
  hazardResolver.io.memCsrStall <> memoryStage.io.memCsrStall
  hazardResolver.io.wbCsrStall <> writeBackStage.io.wb2csr
  hazardResolver.io.memExcMonCheck <> memoryStage.io.excMon
  hazardResolver.io.wbExcMon <> writeBackStage.io.excMonCommit

  // pipeline controller
  pipelineControl.io.ifStallReq     := fetchStage.io.control.stallReq
  pipelineControl.io.exeStallReq    := executeStage.io.control.stallReq
  pipelineControl.io.memStallReq    := memoryStage.io.control.stallReq
  pipelineControl.io.idFlushReq     := decodeStage.io.control.flushIF
  pipelineControl.io.idFlushTarget  := decodeStage.io.control.flushPc
  pipelineControl.io.memFlushReq    := memoryStage.io.control.flushReq
  pipelineControl.io.memFlushTarget := memoryStage.io.control.flushReq

  pipelineControl.io.exceptType := writeBackStage.io.wb2csr.exceptType
  pipelineControl.io.csrInfo <> csrFile.io.csrInfo

  pipelineControl.io.csrHazardFlag   := hazardResolver.io.csrHazardFlag
  pipelineControl.io.loadHazardFlage := hazardResolver.io.loadHazardFlag

  // debug
  io.debug.debugInfo := writeBackStage.io.debug
  io.debug.regs      := regFile.io.debug
  io.debug.csr       := csrFile.io.debug
  io.debug.intr      := timer_intr
}
