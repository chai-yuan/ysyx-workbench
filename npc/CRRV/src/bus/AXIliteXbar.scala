package bus

import chisel3._
import chisel3.util._
import config.CPUconfig._
import io._

object DeviceDefine {
  val DEVICE_WIDTH = log2Ceil(3)

  val DEVICE_NONE   = 0.U(DEVICE_WIDTH.W)
  val DEVICE_RAM    = 1.U(DEVICE_WIDTH.W)
  val DEVICE_SERIAL = 2.U(DEVICE_WIDTH.W)
}

class AXIliteXbar extends Module {
  val io = IO(new Bundle {
    val in     = Flipped(new AXIliteMasterIO(ADDR_WIDTH, DATA_WIDTH))
    val ram    = new AXIliteMasterIO(ADDR_WIDTH, DATA_WIDTH)
    val serial = new AXIliteMasterIO(ADDR_WIDTH, DATA_WIDTH)
  })

  val (sIdle :: sRead :: sWrite :: Nil) = Enum(3)
  val state                             = RegInit(sIdle)
  val device                            = RegInit(DeviceDefine.DEVICE_NONE)

  switch(state) {
    is(sIdle) {
      when(io.in.ar.valid) { state := sRead }
        .elsewhen(io.in.aw.valid) { state := sWrite }
      device := MuxCase(
        DeviceDefine.DEVICE_NONE,
        Seq(
          (io.in.ar.addr(31,28) === "h8".U) -> (DeviceDefine.DEVICE_RAM),
          (io.in.ar.addr(31,28) === "ha".U) -> (DeviceDefine.DEVICE_SERIAL)
        )
      )
    }
    is(sRead) {
      when(io.in.r.valid && io.in.r.ready) {
        state := sIdle
      }
    }
    is(sWrite) {
      when(io.in.b.valid && io.in.b.ready) {
        state := sIdle
      }
    }
  }

  when(state =/= sIdle && device === DeviceDefine.DEVICE_RAM) {
    io.in <> io.ram
    io.serial <> DontCare
  }.elsewhen(state =/= sIdle && device === DeviceDefine.DEVICE_SERIAL) {
    io.in <> io.serial
    io.ram <> DontCare
  }.otherwise{
    io.in <> DontCare
    io.ram <> DontCare
    io.serial <> DontCare
  }
}
