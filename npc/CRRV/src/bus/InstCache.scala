package bus

import chisel3._
import chisel3.util._
import config.CPUconfig._
import io._

/**
  * 直接映射的缓存
  * 没有非缓存地址区域
  * 只读
  *
  * @param cacheLineNum 缓存行数量
  * @param cacheLineSize 缓存行大小(字)
  */
class InstCache(val cacheLineNum: Int, val cacheLineSize: Int) extends Module {
  val io = IO(new Bundle {
    val flush  = Input(Bool())
    val simple = Flipped(new SimpleIO(ADDR_WIDTH, DATA_WIDTH))
    val axi    = new AXI4MasterIO(ADDR_WIDTH, 64)
  })

  val wordWidth     = log2Ceil(INST_WIDTH / 8)
  val lineSizeWidth = log2Ceil(cacheLineSize)
  val lineNumWidth  = log2Ceil(cacheLineNum)
  val tagWidth      = ADDR_WIDTH - lineNumWidth - lineSizeWidth - wordWidth
  val burstSize     = log2Ceil(INST_WIDTH / 8)
  val burstLen      = cacheLineSize - 1

  val sIdle :: sAddr :: sData :: sUpdate :: Nil = Enum(4)
  val state                                     = RegInit(sIdle)

  val valid = RegInit(VecInit(Seq.fill(cacheLineNum) { false.B }))
  val tag   = Mem(cacheLineNum, UInt(tagWidth.W))
  val lines = SyncReadMem(cacheLineNum * cacheLineSize, UInt(INST_WIDTH.W))

  val sramAddr    = Reg(UInt(ADDR_WIDTH.W))
  val selAddr     = Mux(state === sIdle, io.simple.out.bits.addr, sramAddr)
  val tagSel      = selAddr(ADDR_WIDTH - 1, lineNumWidth + lineSizeWidth + wordWidth)
  val lineSel     = selAddr(lineNumWidth + lineSizeWidth + wordWidth - 1, lineSizeWidth + wordWidth)
  val lineDataSel = selAddr(lineSizeWidth + wordWidth - 1, wordWidth)
  val cacheHit    = valid(lineSel) && tag(lineSel) === tagSel

  val ren        = RegInit(false.B)
  val raddr      = Reg(UInt(ADDR_WIDTH.W))
  val dataOffset = Reg(UInt(lineSizeWidth.W))
  val dataSel    = Cat(lineSel, dataOffset)
  val startAddr  = Cat(tagSel, lineSel, 0.U((lineSizeWidth + wordWidth).W))

  switch(state) {
    is(sIdle) {
      when(io.flush) {
        valid.foreach(v => v := false.B)
        state := sIdle
      }.elsewhen(io.simple.out.valid && !cacheHit) {
        ren      := true.B
        raddr    := startAddr
        sramAddr := io.simple.out.bits.addr
        state    := sAddr
      }
    }
    is(sAddr) {
      when(io.axi.ar.ready) {
        ren        := false.B
        dataOffset := 0.U
        state      := sData
      }
    }
    is(sData) {
      when(io.axi.r.valid) {
        dataOffset := dataOffset + 1.U
        // TODO 使用64位，是否有更好处理方法?
        lines.write(dataSel,Mux(dataOffset(0), io.axi.r.bits.data(63, 32), io.axi.r.bits.data(31, 0)))
      }
      when(io.axi.r.valid && io.axi.r.bits.last) {
        state := sUpdate
      }
    }
    is(sUpdate) {
      valid(lineSel) := true.B
      tag(lineSel)   := tagSel
      state          := sIdle
    }
  }

  // simple总线，缓存命中
  io.simple.out.ready := state === sIdle && cacheHit
  io.simple.in.rdata  := lines.read(lineDataSel) // TODO

  // AXI总线，缓存不命中
  io.axi.init()
  io.axi.ar.valid      := ren
  io.axi.ar.bits.addr  := raddr
  io.axi.ar.bits.size  := burstSize.U 
  io.axi.ar.bits.len   := burstLen.U 
  io.axi.ar.bits.burst := 1.U 
  io.axi.r.ready      := true.B
}
