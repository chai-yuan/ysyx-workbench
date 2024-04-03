package bus

import chisel3._
import chisel3.util._
import config.CPUconfig._
import core.define.MemoryControlDefine._
import io._

class SimpleAlignment(val addrWidth: Int = ADDR_WIDTH, val dataWidth: Int = DATA_WIDTH) extends Module {
  val io = IO(new Bundle {
    val in  = Flipped(new SimpleIO(addrWidth, dataWidth))
    val out = new SimpleIO(addrWidth, dataWidth)
  })

  val in  = io.in.out.bits
  val out = io.out.out.bits
  // 处理直接访问
  val lastRaddr = RegInit(0.U(32.W))
  lastRaddr := Mux(io.out.out.ready, in.addr, lastRaddr)
  val shiftData = io.out.in.rdata >> (Cat(lastRaddr(1, 0), 0.U(3.W)))
  // 处理非对齐访问
  val (sIdle :: sSend :: sRead
    :: sReadEnd :: sEnd :: Nil) = Enum(5)
  val state                     = RegInit(sIdle)
  val rawAddr                   = RegInit(0.U(32.W))
  val rawWdata                  = RegInit(0.U(32.W))
  val nowValid                  = RegInit(false.B)
  val nowAddr                   = RegInit(0.U(32.W))
  val nowWen                    = RegInit(false.B)
  val nowWdata                  = RegInit(0.U(32.W))
  val nowRdata                  = RegInit(0.U(32.W))
  val len                       = RegInit(0.U(5.W))
  // 目前仅仅处理4字节非对齐访问
  val noAlignment = (in.addr(1, 0) =/= "b00".U && in.size === LS_DATA_WORD)
  val direct      = (!noAlignment) && (state === sIdle)
  // 非对齐访问状态机
  switch(state) {
    is(sIdle) {
      when(noAlignment) {
        state    := sSend
        rawAddr  := in.addr
        rawWdata := in.wdata
        nowValid := false.B
        nowAddr  := in.addr
        nowWen   := in.writeEn
        len      := (4 - 1).U
      }
    }
    is(sSend) {
      when(!io.out.out.ready) {
        nowValid := true.B
        nowWdata := Fill(4, rawWdata(7, 0))
      }.elsewhen(io.out.out.ready) {
        state    := sRead
        nowValid := false.B
      }
    }
    is(sRead) {
      when(nowAddr === rawAddr + len) {
        state := sReadEnd
      }.otherwise {
        state    := sSend
        nowAddr  := nowAddr + 1.U
        rawWdata := rawWdata >> 8.U
      }
      nowRdata := Cat((io.out.in.rdata >> (Cat(nowAddr(1, 0), 0.U(3.W))))(7, 0), nowRdata(31, 8))
    }
    is(sReadEnd) {
      state    := sEnd
      nowValid := false.B
      nowAddr  := 0.U
      nowWen   := false.B
      nowWdata := 0.U
      len      := 0.U
    }
    is(sEnd) {
      state := sIdle
    }
  }
  // 输入 (已经经过对齐)
  io.in.in.rdata := Mux(direct, shiftData, nowRdata)
  // 输出
  io.out.out.valid := Mux(direct, io.in.out.valid, nowValid)
  io.in.out.ready  := Mux(direct, io.out.out.ready, state === sReadEnd)
  out.addr         := Mux(direct, in.addr, nowAddr)
  out.size         := Mux(direct, in.size, LS_DATA_BYTE)
  out.writeEn      := Mux(direct, in.writeEn, nowWen)
  out.wdata        := Mux(direct, in.wdata, nowWdata)
}
