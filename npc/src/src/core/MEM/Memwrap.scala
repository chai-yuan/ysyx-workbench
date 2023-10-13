package core.MEM

import chisel3._
import chisel3.util._
import core.MemBundle
import core.ID.ControlBundle
import config.MemOp._

class MemWrap extends Module {
  val io = IO(new Bundle {
    val dataMem = new MemBundle

    val control   = Flipped(new ControlBundle)
    val addr      = Input(UInt(32.W))
    val writeData = Input(UInt(32.W))

    val readData = Output(UInt(32.W))
  })

  val control   = io.control
  val addr      = io.addr
  val writeData = io.writeData

  io.dataMem.readEn  := control.memReadEn
  io.dataMem.writeEn := control.memWriteEn
  io.dataMem.addr    := addr

  val mark = MuxCase(
    "b0000".U,
    Seq(
      (control.memOp === MEM_B || control.memOp === MEM_BU) -> ("b0001".U << addr(1, 0)),
      (control.memOp === MEM_H || control.memOp === MEM_HU) -> ("b0011".U << addr(1, 0)),
      (control.memOp === MEM_W) -> ("b1111".U)
    )
  )

  io.dataMem.writeData := MuxCase(
    0.U(32.W),
    Seq(
      (control.memOp === MEM_B || control.memOp === MEM_BU) -> (writeData << Cat(addr(1, 0), 0.U(3.W))),
      (control.memOp === MEM_H || control.memOp === MEM_HU) -> (writeData << Cat(addr(1, 0), 0.U(3.W))),
      (control.memOp === MEM_W) -> writeData
    )
  )

  val readData = io.dataMem.readData << Cat(addr(1, 0), 0.U(3.W))

  io.readData := MuxCase(
    0.U(32.W),
    Seq(
      (control.memOp === MEM_B || control.memOp === MEM_BU) -> (Cat(Fill(24, "b0".U), readData(7, 0))),
      (control.memOp === MEM_H || control.memOp === MEM_HU) -> (Cat(Fill(16, "b0".U), readData(15, 0))),
      (control.memOp === MEM_W) -> readData
    )
  )

  io.dataMem.mark := mark
}
