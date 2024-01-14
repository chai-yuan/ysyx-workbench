package bus

import chisel3._
import chisel3.util._
import config.CPUconfig._
import io._

/**
  * 从Simple接口到AXI 4接口的转换器
  * AXI4的地址宽度被定位 64 位
  * 地址通道通过重新发送，解决了地址变化问题
  */
class Simple2AXI4 extends Module {
  val io = IO(new Bundle {
    val simple = Flipped(new SimpleIO(ADDR_WIDTH, DATA_WIDTH))
    val axi    = new AXI4MasterIO(ADDR_WIDTH, 64)
  })

  val (sIdle :: sRead :: sReadWait
    :: sWrite :: sWriteWait
    :: sEnd :: Nil) = Enum(6)
  val state         = RegInit(sIdle)

  val rdata = Reg(UInt(64.W))
  val addr  = Reg(UInt(ADDR_WIDTH.W))

  switch(state) {
    is(sIdle) {
      when(io.simple.out.valid) {
        state := Mux(io.simple.out.bits.writeEn, sWrite, sRead)
        addr  := io.simple.out.bits.addr
      }
    }
    is(sRead) {
      when(io.axi.ar.ready) {
        state := sReadWait
      }
    }
    is(sReadWait) {
      when(io.axi.r.valid && io.axi.r.bits.last) {
        rdata := io.axi.r.bits.data
        state := sEnd
      }
    }
    is(sWrite) {
      when(io.axi.aw.ready && !io.axi.w.ready) {
        state := sWriteWait
      }.elsewhen(io.axi.aw.ready && io.axi.w.ready) {
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
  io.simple.out.ready := (state === sEnd) && (addr === io.simple.out.bits.addr)
  io.simple.in.rdata  := Mux(addr(2), rdata(63, 32), rdata(31, 0))

  val wdata    = Cat(io.simple.out.bits.wdata, io.simple.out.bits.wdata)
  val dataSize = io.simple.out.bits.size
  val wstrb = MuxLookup(dataSize, 0.U)(
    Seq(
      0.U -> ("b0001".U(8.W) << addr(2, 0)),
      1.U -> ("b0011".U(8.W) << addr(2, 0)),
      2.U -> ("b1111".U(8.W) << addr(2, 0))
    )
  )

  io.axi.init()
  io.axi.ar.valid      := state === sRead
  io.axi.ar.bits.addr  := addr
  io.axi.ar.bits.size  := dataSize
  io.axi.r.ready       := true.B
  io.axi.aw.valid      := state === sWrite
  io.axi.aw.bits.addr  := addr
  io.axi.aw.bits.size  := dataSize
  io.axi.w.valid       := state === sWrite || state === sWriteWait
  io.axi.w.bits.data   := wdata
  io.axi.w.bits.last   := state === sWrite || state === sWriteWait
  io.axi.w.bits.strb   := wstrb
  io.axi.b.ready       := true.B
}
