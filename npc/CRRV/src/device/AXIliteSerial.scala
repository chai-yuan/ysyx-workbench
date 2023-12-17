package device


import chisel3._
import chisel3.util._
import io.SimpleMemIO
import config.CPUconfig._
import io.AXIliteMasterIO
import sim.DPIC_serial

class AXIliteSerial extends Module {
  val io = IO(Flipped(new AXIliteMasterIO(ADDR_WIDTH, DATA_WIDTH)))

  val device = Module(new DPIC_serial)
  device.io.clock := clock
  device.io.reset := reset

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

  device.io.ren   := readState === sReadData
  device.io.raddr := raddr
  io.r.data    := device.io.rdata
  io.r.resp    := 0.U

  io.ar.ready := readState === sReadAddr
  io.r.valid  := readState === sReadData

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

  device.io.wen   := writeState === sWriteEnd
  device.io.waddr := waddr
  device.io.wdata := wdata
  device.io.wmask := wmask
  io.b.valid   := writeState === sWriteEnd
  io.b.resp    := 0.U

  io.aw.ready := writeState === sWriteAddr
  io.w.ready  := writeState === sWriteData
}
