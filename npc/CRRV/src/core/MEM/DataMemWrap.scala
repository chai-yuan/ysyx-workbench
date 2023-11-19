package core.MEM

import chisel3._
import chisel3.util._
import core.ID.ControlBundle
import config.MemOp._
import memory.AddrBundle
import memory.ReadBundle
import memory.WriteBackBundle
import memory.WriteBundle

class DataMemWriteWrap extends Module {
  val io = IO(new Bundle {
    val dataMemAR = new AddrBundle
    val dataMemAW = new AddrBundle
    val dataMemW  = new WriteBundle

    val control   = Flipped(new ControlBundle)
    val addr      = Input(UInt(32.W))
    val writeData = Input(UInt(32.W))

    val stall = Output(Bool())
  })

  val control   = io.control
  val addr      = io.addr
  val writeData = io.writeData

  io.dataMemAR.valid := control.memReadEn
  io.dataMemAR.addr  := addr
  io.dataMemAW.valid := control.memWriteEn
  io.dataMemW.valid  := control.memWriteEn
  io.dataMemAW.addr  := addr

  val mask = MuxCase(
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

  io.dataMemW.data := MuxCase(
    0.U(32.W),
    Seq(
      (control.memOp === MEM_B || control.memOp === MEM_BU) -> (Fill(4, writeData(7, 0))),
      (control.memOp === MEM_H || control.memOp === MEM_HU) -> (Fill(2, writeData(15, 0))),
      (control.memOp === MEM_W) -> writeData
    )
  )

  io.dataMemW.strb := mask

  // 如果没有握手成功，那么等待
  io.stall := (io.dataMemAR.valid && !io.dataMemAR.ready) ||
    (io.dataMemAW.valid && !io.dataMemAW.ready) ||
    (io.dataMemW.valid && !io.dataMemW.ready)
}

class DataMemReadWrap extends Module {
  val io = IO(new Bundle {
    val dataMemR = new ReadBundle
    val dataMemB = new WriteBackBundle

    val control  = Flipped(new ControlBundle)
    val addr     = Input(UInt(32.W))
    val readData = Output(UInt(32.W))

    val stall   = Output(Bool())
    val allowin = Input(Bool())
  })

  val control     = io.control
  val addr        = io.addr
  val rawReadData = Mux(io.dataMemR.valid, io.dataMemR.data, 0.U)

  val shiftData = rawReadData >> (Cat(addr(1, 0), 0.U(3.W)))
  io.readData := MuxCase(
    shiftData,
    Seq(
      (control.memOp === MEM_B) -> (Cat(Fill(24, shiftData(7)), shiftData(7, 0))),
      (control.memOp === MEM_BU) -> (Cat(0.U(24.W), shiftData(7, 0))),
      (control.memOp === MEM_H) -> (Cat(Fill(16, shiftData(15)), shiftData(15, 0))),
      (control.memOp === MEM_HU) -> (Cat(0.U(16.W), shiftData(15, 0))),
      (control.memOp === MEM_W) -> (shiftData)
    )
  )

  io.stall          := (control.memReadEn && !io.dataMemR.valid)
  io.dataMemR.ready := io.dataMemR.valid && io.allowin
  io.dataMemB.ready := true.B
}
