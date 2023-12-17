package bus

import chisel3._
import chisel3.util._
import config.CPUconfig._
import io._
import device.DeviceDefine._

class AXIliteXbar extends Module {
  val io = IO(new Bundle {
    val in     = Flipped(new AXIliteMasterIO(ADDR_WIDTH, DATA_WIDTH))
    val ram    = new AXIliteMasterIO(ADDR_WIDTH, DATA_WIDTH)
    val serial = new AXIliteMasterIO(ADDR_WIDTH, DATA_WIDTH)
    val clint  = new AXIliteMasterIO(ADDR_WIDTH, DATA_WIDTH)
  })

  val (sIdle :: sRead :: sWrite :: Nil) = Enum(3)
  val state                             = RegInit(sIdle)
  val device                            = RegInit(DEVICE_NONE)

  switch(state) {
    is(sIdle) {
      when(io.in.ar.valid) { state := sRead }
        .elsewhen(io.in.aw.valid) { state := sWrite }
      device := MuxCase(
        DEVICE_NONE,
        Seq(
          (io.in.ar.addr(31, 28) === "h8".U) -> (DEVICE_RAM),
          (io.in.ar.addr(31, 28) === "ha".U) -> (DEVICE_SERIAL)
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

  when(state =/= sIdle && device === DEVICE_RAM) {
    io.in <> io.ram
    io.serial.setAXIliteDefaults()
    io.clint.setAXIliteDefaults()
  }.elsewhen(state =/= sIdle && device === DEVICE_SERIAL) {
    io.in <> io.serial
    io.ram.setAXIliteDefaults()
    io.clint.setAXIliteDefaults()
  }.otherwise {
    io.in <> io.clint
    io.ram.setAXIliteDefaults()
    io.serial.setAXIliteDefaults()
  }
}
