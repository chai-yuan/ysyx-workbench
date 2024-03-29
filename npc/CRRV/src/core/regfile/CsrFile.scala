package core.regfile

import chisel3._
import chisel3.util._
import io._
import config.CPUconfig._
import core.define.CsrDefine._
import core.define.OperationDefine._
import core.define.InstructionDefine

class CsrFile extends Module {
  val io = IO(new Bundle {
    val read  = Flipped(new CsrReadIO)
    val write = Flipped(new CsrWriteIO)

    val csrInfo = new CsrInfoIO
  })

  val mstatus   = RegInit(0.U(32.W))
  val mtvec     = RegInit(0.U(32.W))
  val mepc      = RegInit(0.U(32.W))
  val mcause    = RegInit(0.U(32.W))
//   val mvendorid = RegInit(0x78797379.U(32.W))
//   val marchid   = RegInit(0x015fde2e.U(32.W))
  val mvendorid = RegInit(0.U(32.W))
  val marchid   = RegInit(0.U(32.W))
  // 处理读取数据
  val readData = MuxLookup(io.read.addr, 0.U) {
    Seq(
      (CSR_MSTATUS) -> (mstatus),
      (CSR_MTVEC) -> (mtvec),
      (CSR_MEPC) -> (mepc),
      (CSR_MCAUSE) -> (mcause),
      (CSR_MVENDORID) -> (mvendorid),
      (CSR_MARCHID) -> (marchid)
    )
  }
  val readValid = true.B
  // 处理写入数据
  val writeEn = dontTouch(io.write.op =/= CSR_NOP && io.write.op =/= CSR_R)
  val writeData = MuxLookup(io.write.op, 0.U)(
    Seq(
      CSR_W -> io.write.data,
      CSR_RW -> io.write.data,
      CSR_RS -> (readData | io.write.data),
      CSR_RC -> (readData & ~io.write.data)
    )
  )
  // 控制状态更新 异常更新优先
  when(io.write.exceptType =/= EXC_NONE) { // 异常更新
    when(io.write.exceptType === EXC_ECALL) {
      mepc   := io.write.exceptPc
      mcause := 11.U(32.W)
    }
  }.elsewhen(writeEn) { // 写入更新
    when(io.write.addr === CSR_MSTATUS) { mstatus := writeData }
    when(io.write.addr === CSR_MTVEC) { mtvec := writeData }
    when(io.write.addr === CSR_MEPC) { mepc := writeData }
    when(io.write.addr === CSR_MCAUSE) { mcause := writeData }
  }

  io.read.valid := readValid
  io.read.data  := readData

  io.csrInfo.mepc         := mepc
  io.csrInfo.trapEnterVec := mtvec
}

class CsrInfoIO extends Bundle {
  val mepc         = Output(UInt(ADDR_WIDTH.W))
  val trapEnterVec = Output(UInt(ADDR_WIDTH.W))
}
