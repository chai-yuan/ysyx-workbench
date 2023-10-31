package memory

import chisel3._
import chisel3.util._

class DPIC_RAM extends BlackBox {
  val io = IO(new Bundle {
    val ren   = Input(Bool())
    val raddr = Input(UInt(32.W))
    val rdata = Output(UInt(32.W))

    val wen   = Input(Bool())
    val waddr = Input(UInt(32.W))
    val wdata = Input(UInt(32.W))
    val wmask = Input(UInt(4.W))
  })

  override def desiredName: String = "dpic_ram"
}
