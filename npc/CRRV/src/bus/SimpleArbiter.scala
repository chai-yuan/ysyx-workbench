package bus

import chisel3._
import chisel3.util._
import config.CPUconfig._
import io._

/**
  * Simple总线 仲裁器
  * 数据端口优先
  */
class SimpleArbiter extends Module {
  val io = IO(new Bundle {
    val simpleInst = Flipped(new SimpleMemIO(ADDR_WIDTH, DATA_WIDTH))
    val simpleData = Flipped(new SimpleMemIO(ADDR_WIDTH, DATA_WIDTH))
    val simpleOut  = new SimpleMemIO(ADDR_WIDTH, DATA_WIDTH)
  })

  val (sIdle :: sInst :: sData
    :: sInstEnd :: sDataEnd :: Nil) = Enum(5)
  val state                         = RegInit(sIdle)

  switch(state) {
    is(sIdle) {
      when(io.simpleData.enable) { state := sData }
        .elsewhen(io.simpleInst.enable) { state := sInst }
    }
    is(sInst) {
      when(io.simpleInst.valid) {
        state := sInstEnd
      }
    }
    is(sData) {
      when(io.simpleData.valid) {
        state := sDataEnd
      }
    }
    is(sInstEnd) {
      state := sIdle
    }
    is(sDataEnd) {
      state := sIdle
    }
  }

  val selInst = (state === sInst) || (state === sInstEnd)
  val selData = (state === sData) || (state === sDataEnd)

  io.simpleInst.valid := selInst && io.simpleOut.valid
  io.simpleInst.rdata := io.simpleOut.rdata

  io.simpleData.valid := selData && io.simpleOut.valid
  io.simpleData.rdata := io.simpleOut.rdata

  io.simpleOut.enable := MuxCase(
    false.B,
    Seq(
      (selInst) -> (io.simpleInst.enable),
      (selData) -> (io.simpleData.enable)
    )
  )
  io.simpleOut.wen   := Mux(selData, io.simpleData.wen, io.simpleInst.wen)
  io.simpleOut.addr  := Mux(selData, io.simpleData.addr, io.simpleInst.addr)
  io.simpleOut.wdata := Mux(selData, io.simpleData.wdata, io.simpleInst.wdata)

}
