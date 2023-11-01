package memory

import chisel3._
import chisel3.util._

class REG_RAM extends Module {
  val io = IO(new Bundle {
    val ren   = Input(Bool())
    val raddr = Input(UInt(32.W))
    val rdata = Output(UInt(32.W))

    val wen   = Input(Bool())
    val waddr = Input(UInt(32.W))
    val wdata = Input(UInt(32.W))
    val wmask = Input(UInt(4.W))
  })

  val regmem = Mem(256, UInt(32.W))

  val fullmask = Cat(Fill(8, io.wmask(3)), Fill(8, io.wmask(2)), Fill(8, io.wmask(1)), Fill(8, io.wmask(0)))
  when(io.wen) {
    regmem(io.waddr) := (io.wdata & fullmask) | (regmem(io.waddr) & ~fullmask)
  }

  io.rdata := Mux(io.ren, regmem(io.raddr), 0.U)
}
