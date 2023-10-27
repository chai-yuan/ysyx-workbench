package core.MEM

import chisel3._
import chisel3.util._
import core.MemBundle
import core.ID.ControlBundle
import config.MemOp._
import os.read

class DataMemWriteWrap extends Module {
  val io = IO(new Bundle {
    val dataMem = new MemBundle

    val control   = Flipped(new ControlBundle)
    val addr      = Input(UInt(32.W))
    val writeData = Input(UInt(32.W))

    val rawReadData = Output(UInt(32.W))
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
      (control.memOp === MEM_B || control.memOp === MEM_BU) -> (Fill(4, writeData(7, 0))),
      (control.memOp === MEM_H || control.memOp === MEM_HU) -> (Fill(2, writeData(15, 0))),
      (control.memOp === MEM_W) -> writeData
    )
  )
  io.rawReadData  := io.dataMem.readData
  io.dataMem.mark := mark
}

class DataMemReadWrap extends Module {
  val io = IO(new Bundle {
    val rawReadData = Input(UInt(32.W))

    val control  = Flipped(new ControlBundle)
    val addr     = Input(UInt(32.W))
    val readData = Output(UInt(32.W))
  })

  val control     = io.control
  val addr        = io.addr
  val rawReadData = io.rawReadData

  val shiftData = rawReadData >> (Cat(addr(1, 0), 0.U(3.W)))
  io.readData := MuxCase(
    shiftData,
    Seq(
      (control.memOp === MEM_B) -> (Cat(Fill(24, shiftData(7)), shiftData(7, 0))),
      (control.memOp === MEM_H) -> (Cat(Fill(16, shiftData(15)), shiftData(15, 0))),
      (control.memOp === MEM_BU || control.memOp === MEM_HU || control.memOp === MEM_W) -> (shiftData)
    )
  )

}
