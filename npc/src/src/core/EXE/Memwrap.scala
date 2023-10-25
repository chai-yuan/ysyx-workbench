package core.EXE

import chisel3._
import chisel3.util._
import core.MemBundle
import core.ID.ControlBundle
import config.MemOp._
import os.read

class MemWrap extends Module {
  val io = IO(new Bundle {
    val dataMem = new MemBundle

    val valid = Input(Bool())
    val control = Flipped(new ControlBundle)
    val addr = Input(UInt(32.W))
    val writeData = Input(UInt(32.W))

    val readData = Output(UInt(32.W))
  })

  val control = io.control
  val addr = io.addr
  val writeData = io.writeData

  io.dataMem.readEn := control.memReadEn && io.valid
  io.dataMem.writeEn := control.memWriteEn && io.valid
  io.dataMem.addr := addr

  val mark = MuxCase(
    "b0000".U,
    Seq(
      (control.memOp === MEM_B || control.memOp === MEM_BU) -> ("b0001".U << addr(
        1,
        0
      )),
      (control.memOp === MEM_H || control.memOp === MEM_HU) -> ("b0011".U << addr(
        1,
        0
      )),
      (control.memOp === MEM_W) -> ("b1111".U)
    )
  )

  io.dataMem.writeData := MuxCase(
    0.U(32.W),
    Seq(
      (control.memOp === MEM_B || control.memOp === MEM_BU) -> (writeData << Cat(
        addr(1, 0),
        0.U(3.W)
      )),
      (control.memOp === MEM_H || control.memOp === MEM_HU) -> (writeData << Cat(
        addr(1, 0),
        0.U(3.W)
      )),
      (control.memOp === MEM_W) -> writeData
    )
  )
  io.readData := io.dataMem.readData
  io.dataMem.mark := mark
}
