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
    val simpleInst = Flipped(new SimpleIO(ADDR_WIDTH, DATA_WIDTH))
    val simpleData = Flipped(new SimpleIO(ADDR_WIDTH, DATA_WIDTH))
    val simpleOut  = new SimpleIO(ADDR_WIDTH, DATA_WIDTH)
  })

  val (sIdle :: sInst :: sData
    :: sInstEnd :: sDataEnd :: Nil) = Enum(5)
  val state                         = RegInit(sIdle)

  switch(state) {
    is(sIdle) {
      when(io.simpleData.out.valid) { state := sData }
        .elsewhen(io.simpleInst.out.valid) { state := sInst }
    }
    is(sInst) {
      when(io.simpleInst.out.fire) {
        state := sInstEnd
      }
    }
    is(sData) {
      when(io.simpleData.out.fire) {
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

  io.simpleInst.out.ready := selInst && io.simpleOut.out.ready
  io.simpleInst.in.rdata  := io.simpleOut.in.rdata

  io.simpleData.out.ready := selData && io.simpleOut.out.ready
  io.simpleData.in.rdata  := io.simpleOut.in.rdata

  io.simpleOut.out.valid        := state === sInst || state === sData
  io.simpleOut.out.bits.size    := Mux(selData, io.simpleData.out.bits.size, io.simpleInst.out.bits.size)
  io.simpleOut.out.bits.addr    := Mux(selData, io.simpleData.out.bits.addr, io.simpleInst.out.bits.addr)
  io.simpleOut.out.bits.writeEn := Mux(selData, io.simpleData.out.bits.writeEn, io.simpleInst.out.bits.writeEn)
  io.simpleOut.out.bits.wdata   := Mux(selData, io.simpleData.out.bits.wdata, io.simpleInst.out.bits.wdata)
}
