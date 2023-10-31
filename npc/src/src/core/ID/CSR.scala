package core.ID

import chisel3._
import chisel3.util._
import config.CSRCodes
import config.Inst

class CSR extends Module {
  val io = IO(new Bundle {
    val inst    = Input(UInt(32.W))
    val pc      = Input(UInt(32.W))
    val csrAddr = Input(UInt(32.W))
    val src1    = Input(UInt(32.W))
    val csrRead = Output(UInt(32.W))
  })

  val mepc    = RegInit(0.U(32.W))
  val mcause  = RegInit(0.U(32.W))
  val mstatus = RegInit(0.U(32.W))
  val mtvec   = RegInit(0.U(32.W))

  val inst    = io.inst
  val pc      = io.pc
  val csrAddr = io.csrAddr

  val readData = MuxCase(
    0.U,
    Seq(
      (inst === Inst.ECALL) -> mtvec,
      (inst === Inst.MRET) -> mepc,
      (csrAddr === CSRCodes.CSR_MEPC) -> mepc,
      (csrAddr === CSRCodes.CSR_MCAUSE) -> mcause,
      (csrAddr === CSRCodes.CSR_MSTATUS) -> mstatus,
      (csrAddr === CSRCodes.CSR_MTVEC) -> mtvec
    )
  )

  val isZicsr = (inst === Inst.CSRRS || inst === Inst.CSRRW)
  val newVal = MuxCase(
    0.U,
    Seq(
      (inst === Inst.CSRRW) -> (io.src1),
      (inst === Inst.CSRRS) -> (io.src1 | readData)
    )
  )

  mepc := MuxCase(
    mepc,
    Seq(
      (isZicsr && csrAddr === CSRCodes.CSR_MEPC) -> (newVal),
      (inst === Inst.ECALL) -> pc
    )
  )
  mcause := MuxCase(
    mcause,
    Seq(
      (isZicsr && csrAddr === CSRCodes.CSR_MCAUSE) -> (newVal),
      (inst === Inst.ECALL) -> 11.U(32.W)
    )
  )
  mstatus := MuxCase(
    mstatus,
    Seq(
      (isZicsr && csrAddr === CSRCodes.CSR_MSTATUS) -> (newVal),
      (inst === Inst.ECALL) -> "h1800".U
    )
  )
  mtvec := MuxCase(
    mtvec,
    Seq(
      (isZicsr && csrAddr === CSRCodes.CSR_MTVEC) -> (newVal)
    )
  )

  io.csrRead := readData
}
