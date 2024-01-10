package bus

import chisel3._
import chisel3.util._
import config.CPUconfig._
import io._

/**
  * 从Simple接口到AXI 4接口的转换器
  * 地址通道通过重新发送，解决了地址变化问题
  */
class Simple2AXI4 extends Module {
  val io = IO(new Bundle {
    val simple = Flipped(new SimpleMemIO(ADDR_WIDTH, DATA_WIDTH))
    val axi    = new AXI4MasterIO(ADDR_WIDTH, DATA_WIDTH)
  })

  val (sIdle :: sReadAddr :: sReadData
    :: sWrite :: sWriteWait
    :: sEnd :: Nil) = Enum(6)
  val state         = RegInit(sIdle)

  val rdata = Reg(UInt(DATA_WIDTH.W))
  val addr  = Reg(UInt(ADDR_WIDTH.W))

  switch(state) {
    is(sIdle) {
      when(io.simple.enable) {
        state := Mux(io.simple.wen =/= 0.U, sWrite, sReadAddr)
        addr  := io.simple.addr
      }
    }
    is(sReadAddr) {
      when(io.axi.ar.ready) {
        state := sReadData
      }
    }
    is(sReadData) {
      when(io.axi.r.valid && io.axi.r.bits.last) {
        rdata := io.axi.r.bits.data
        state := sEnd
      }
    }
    is(sWrite) {
      when(io.axi.aw.ready && !io.axi.w.ready) {
        state := sWriteWait
      }.elsewhen(io.axi.aw.ready && io.axi.w.ready){
        state := sEnd
      }
    }
    is(sWriteWait) {
      when(io.axi.w.ready) {
        state := sEnd
      }
    }
    is(sEnd) {
      state := sIdle
    }
  }
  // 如果地址有变动，则需要重新发送读写
  io.simple.valid := (state === sEnd) && (addr === io.simple.addr)
  io.simple.rdata := rdata

  val dataSize = log2Ceil(DATA_WIDTH / 8)
  io.axi.init()
  io.axi.ar.valid      := state === sReadAddr
  io.axi.ar.bits.addr  := addr
  io.axi.ar.bits.size  := dataSize.U
  io.axi.ar.bits.burst := 1.U // incrementing-address
  io.axi.r.ready       := true.B
  io.axi.aw.valid      := state === sWrite
  io.axi.aw.bits.addr  := addr
  io.axi.aw.bits.size  := dataSize.U
  io.axi.w.valid       := state === sWrite || state === sWriteWait
  io.axi.w.bits.data   := io.simple.wdata
  io.axi.w.bits.last   := state === sWrite || state === sWriteWait
  io.axi.w.bits.strb   := io.simple.wen
  io.axi.b.ready       := true.B
}
