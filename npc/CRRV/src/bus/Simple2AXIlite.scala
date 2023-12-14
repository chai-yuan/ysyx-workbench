package bus

import chisel3._
import chisel3.util._
import config.CPUconfig._
import io._

/**
 * 从Simple接口到AXI lite接口的转换器
  */
class Simple2AXIlite extends Module {
  val io = IO(new Bundle {
    val simple  = Flipped(new SimpleMemIO(ADDR_WIDTH, DATA_WIDTH))
    val axilite = new AXIliteMasterIO(ADDR_WIDTH, DATA_WIDTH)
  })

  val (sIdle :: sReadAddr :: sReadData
    :: sWriteAddr :: sWriteData
    :: sReadEnd :: sWriteEnd :: Nil) = Enum(7)
  val state                          = RegInit(sIdle)

  val rdata = Reg(UInt(DATA_WIDTH.W))

  switch(state) {
    is(sIdle) {
      when(io.simple.enable) {
        state := Mux(io.simple.wen =/= 0.U, sWriteAddr, sReadAddr)
      }
    }
    is(sReadAddr) {
      when(io.axilite.ar.ready) {
        state := sReadData
      }
    }
    is(sReadData) {
      when(io.axilite.r.valid) {
        rdata := io.axilite.r.data
        state := sReadEnd
      }
    }
    is(sWriteAddr) {
      when(io.axilite.aw.ready) {
        state := sWriteData
      }
    }
    is(sWriteData) {
      when(io.axilite.w.ready) {
        state := sWriteEnd
      }
    }
    is(sReadEnd) {
      state := sIdle
    }
    is(sWriteEnd) {
      state := sIdle
    }
  }

  io.simple.valid := state === sWriteEnd || io.axilite.r.valid
  io.simple.rdata := rdata

  io.axilite.ar.valid := state === sReadAddr
  io.axilite.ar.addr  := io.simple.addr
  io.axilite.r.ready  := true.B
  io.axilite.aw.valid := state === sWriteAddr
  io.axilite.aw.addr  := io.simple.addr
  io.axilite.w.valid  := state === sWriteData
  io.axilite.w.data   := io.simple.wdata
  io.axilite.w.strb   := io.simple.wen
  io.axilite.b.ready  := true.B
}
