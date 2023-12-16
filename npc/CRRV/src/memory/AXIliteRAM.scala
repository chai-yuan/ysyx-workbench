package memory

import chisel3._
import chisel3.util._
import io.SimpleMemIO
import chisel3.util.random.LFSR
import config.CPUconfig._
import sim.DPIC_RAM
import io.AXIliteMasterIO

class AXIliteRAM(randomDelayEnable: Boolean) extends Module {
  val io = IO(Flipped(new AXIliteMasterIO(ADDR_WIDTH, DATA_WIDTH)))

  val randomDelay = Wire(UInt(4.W))
  if (randomDelayEnable) {
    randomDelay := LFSR(4, true.B)
  } else {
    randomDelay := 0.U
  }

  val ram = Module(new DPIC_RAM)
  ram.io.clock := clock
  ram.io.reset := reset

  val (sIdle :: sReadAddr :: sReadData
    :: sWriteAddr :: sWriteData
    :: sReadEnd :: sWriteEnd :: Nil) = Enum(7)
  val readState                      = RegInit(sIdle)
  val writeState                     = RegInit(sIdle)

  val raddr = Reg(UInt(ADDR_WIDTH.W))

  switch(readState) {
    is(sIdle) {
      when(io.ar.valid) {
        readState := sReadAddr
      }
    }
    is(sReadAddr) {
      when(io.ar.ready) {
        readState := sReadData
        raddr     := io.ar.addr
      }
    }
    is(sReadData) {
      when(io.r.valid && io.r.ready) {
        readState := sReadEnd
      }
    }
    is(sReadEnd) {
      readState := sIdle
    }
  }

  ram.io.ren   := readState === sReadData
  ram.io.raddr := raddr
  io.r.data    := ram.io.rdata
  io.r.resp    := 0.U
  // 随机化延迟
  io.ar.ready := readState === sReadAddr && !randomDelay(0)
  io.r.valid  := readState === sReadData && !randomDelay(1)

  val waddr = Reg(UInt(ADDR_WIDTH.W))
  val wdata = Reg(UInt(DATA_WIDTH.W))
  val wmask = Reg(UInt((DATA_WIDTH / 8).W))

  switch(writeState) {
    is(sIdle) {
      when(io.aw.valid) {
        writeState := sWriteAddr
      }
    }
    is(sWriteAddr) {
      when(io.aw.ready) {
        writeState := sWriteData
        waddr      := io.aw.addr
      }
    }
    is(sWriteData) {
      when(io.w.valid && io.w.ready) {
        writeState := sWriteEnd
        wdata      := io.w.data
        wmask      := io.w.strb
      }
    }
    is(sWriteEnd) {
      writeState := sIdle
    }
  }

  ram.io.wen   := writeState === sWriteEnd
  ram.io.waddr := waddr
  ram.io.wdata := wdata
  ram.io.wmask := wmask
  io.b.valid   := writeState === sWriteEnd
  io.b.resp    := 0.U
  // 随机化延迟
  io.aw.ready := writeState === sWriteAddr && !randomDelay(2)
  io.w.ready  := writeState === sWriteData && !randomDelay(3)
}
