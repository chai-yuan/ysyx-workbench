package bundle

import chisel3._

class ResultBundle extends Bundle {
  val regDataWrite = Output(UInt(32.W))
  val nextPC       = Output(UInt(32.W))
}
