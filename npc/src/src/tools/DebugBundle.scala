import chisel3._

class DebugBundle extends Bundle {
  val debugPC   = Output(UInt(32.W))
  val debugHalt = Output(Bool())
}
