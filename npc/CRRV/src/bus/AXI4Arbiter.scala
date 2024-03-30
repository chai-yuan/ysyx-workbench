package bus

import chisel3._
import chisel3.util._
import config.CPUconfig._
import io._
import os.stat

/**
  * AXI4总线 仲裁器
  * 数据端口优先
  */
class AXI4Arbiter extends Module {
  val io = IO(new Bundle {
    val axiInst = Flipped(new AXI4MasterIO(ADDR_WIDTH, 64))
    val axiData = Flipped(new AXI4MasterIO(ADDR_WIDTH, 64))
    val axiOut  = new AXI4MasterIO(ADDR_WIDTH, 64)
  })

  val inst       = io.axiInst
  val data       = io.axiData
  val out        = io.axiOut
  val axiInstReq = inst.ar.valid || inst.aw.valid
  val axiDataReq = data.ar.valid || data.aw.valid
  val axiInstEnd = (inst.r.valid && inst.r.ready && inst.r.bits.last)
  val axiDataEnd = (data.b.valid && data.b.ready) || (data.r.valid && data.r.ready && data.r.bits.last)

  val (sIdle :: sInst :: sData
    :: sInstEnd :: sDataEnd :: Nil) = Enum(5)
  val state                         = RegInit(sIdle)
  switch(state) {
    is(sIdle) {
      when(axiDataReq && !axiDataEnd) { state := sData }
        .elsewhen(axiInstReq && !axiInstEnd) { state := sInst }
    }
    is(sInst) {
      when(axiInstEnd) {
        state := sInstEnd
      }
    }
    is(sData) {
      when(axiDataEnd) {
        state := sDataEnd
      }
    }
    is(sInstEnd) {
      when(axiDataReq && !axiDataEnd) { state := sData }
        .elsewhen(axiInstReq && !axiInstEnd) { state := sInst }
        .otherwise { state := sIdle }
    }
    is(sDataEnd) {
      when(axiDataReq && !axiDataEnd) { state := sData }
        .elsewhen(axiInstReq && !axiInstEnd) { state := sInst }
        .otherwise { state := sIdle }
    }
  }

  val selData = (state === sData) || (state === sDataEnd) ||
    (axiDataReq && (state === sIdle))
  val selInst = ((state === sInst) || (state === sInstEnd) ||
    (axiInstReq && (state === sIdle))) &&
    !selData

  inst.ar.ready := selInst && out.ar.ready
  inst.r.valid  := selInst && out.r.valid
  inst.aw.ready := false.B
  inst.w.ready  := false.B
  inst.b.valid  := false.B
  inst.r.bits   := out.r.bits
  inst.b.bits   := out.b.bits

  data.ar.ready := selData && out.ar.ready
  data.r.valid  := selData && out.r.valid
  data.aw.ready := selData && out.aw.ready
  data.w.ready  := selData && out.w.ready
  data.b.valid  := selData && out.b.valid
  data.r.bits   := out.r.bits
  data.b.bits   := out.b.bits

  out.ar.valid := Mux(selData, data.ar.valid, inst.ar.valid)
  out.r.ready  := Mux(selData, data.r.ready, inst.r.ready)
  out.aw.valid := Mux(selData, data.aw.valid, inst.aw.valid)
  out.w.valid  := Mux(selData, data.w.valid, inst.w.valid)
  out.b.ready  := Mux(selData, data.b.ready, inst.b.ready)

  out.ar.bits := Mux(selData, data.ar.bits, inst.ar.bits)
  out.aw.bits := Mux(selData, data.aw.bits, inst.aw.bits)
  out.w.bits  := Mux(selData, data.w.bits, inst.w.bits)
}
