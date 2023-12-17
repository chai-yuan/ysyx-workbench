package device

import chisel3._
import chisel3.util._

object DeviceDefine {
  val DEVICE_WIDTH = log2Ceil(3)

  val DEVICE_NONE   = 0.U(DEVICE_WIDTH.W)
  val DEVICE_RAM    = 1.U(DEVICE_WIDTH.W)
  val DEVICE_SERIAL = 2.U(DEVICE_WIDTH.W)
}
