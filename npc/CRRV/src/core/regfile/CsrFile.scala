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

    val intr    = new InterruptIO
    val csrInfo = new CsrInfoIO
  })
  // CSR
  val mstatus   = RegInit(0.U(32.W))
  val mcause    = RegInit(0.U(32.W))
  val mtvec     = RegInit(0.U(32.W))
  val mepc      = RegInit(0.U(32.W))
  val mvendorid = RegInit(0.U(32.W))
  val marchid   = RegInit(0.U(32.W))
  val mscratch  = RegInit(0.U(32.W))
  val mie       = RegInit(0.U(32.W))
  val mip       = RegInit(0.U(32.W))
  val mtval     = RegInit(0.U(32.W))
  // 处理器状态
  val privilege = RegInit(CSR_MODE_M)
  val sleep     = RegInit(false.B)
  // 处理读取数据
  val readData = MuxLookup(io.read.addr, 0.U) {
    Seq(
      (CSR_MSTATUS) -> (mstatus),
      (CSR_MCAUSE) -> (mcause),
      (CSR_MTVEC) -> (mtvec),
      (CSR_MEPC) -> (mepc),
      (CSR_MVENDORID) -> (mvendorid),
      (CSR_MARCHID) -> (marchid),
      (CSR_MSCRATCH) -> (mscratch),
      (CSR_MIE) -> (mie),
      (CSR_MIP) -> (mip),
      (CSR_MTVAL) -> (mtval)
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
  // 中断更新
  when(io.intr.timer) {
    mip   := mip | (1 << 7).U
    sleep := false.B
  }.otherwise {
    mip := mip & ~(1 << 7).U
  }
  val intr = mip(7) && mie(7) && mstatus(3)
  // 控制状态更新
  when(intr) { // 中断更新
    mepc      := io.write.exceptPc
    mcause    := "h8000_0007".U(32.W)
    mtval     := 0.U
    mstatus   := ((mstatus & "h08".U) << 4.U) | (privilege << 11.U)
    privilege := CSR_MODE_M
  }
    .elsewhen(io.write.exceptType =/= EXC_NONE) { // 异常更新
      when(io.write.exceptType === EXC_ECALL) {
        mepc      := io.write.exceptPc
        mcause    := Mux(privilege === CSR_MODE_M, 11.U, 8.U)
        mtval     := io.write.exceptPc
        mstatus   := ((mstatus & "h08".U) << 4.U) | (privilege << 11.U)
        privilege := CSR_MODE_M
      }
    }
    .elsewhen(writeEn) { // 写入更新
      when(io.write.addr === CSR_MSTATUS) { mstatus := writeData }
      when(io.write.addr === CSR_MTVEC) { mtvec := writeData }
      when(io.write.addr === CSR_MEPC) { mepc := writeData }
      when(io.write.addr === CSR_MCAUSE) { mcause := writeData }
      when(io.write.addr === CSR_MSCRATCH) { mscratch := writeData }
      when(io.write.addr === CSR_MIE) { mie := writeData }
      when(io.write.addr === CSR_MTVAL) { mtval := writeData }
    }

  io.read.valid := readValid
  io.read.data  := readData

  io.csrInfo.mepc         := mepc
  io.csrInfo.trapEnterVec := mtvec
  io.csrInfo.intr         := intr
  io.csrInfo.sleep        := sleep
}

class CsrInfoIO extends Bundle {
  val mepc         = Output(UInt(ADDR_WIDTH.W))
  val trapEnterVec = Output(UInt(ADDR_WIDTH.W))
  val intr         = Output(Bool())
  val sleep        = Output(Bool())
}
