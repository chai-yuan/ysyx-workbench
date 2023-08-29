import chisel3._
import tools.ControlBundle
import tools.DebugBundle

class Execute extends Module {
  val io = IO(new Bundle {
    val regSrc1       = Input(UInt(32.W))
    val regSrc2       = Input(UInt(32.W))
    val imm           = Input(UInt(32.W))
    val controlBundle = Flipped(new ControlBundle())
    val regDataWrite  = Output(UInt(32.W))
  })

  io.regDataWrite := io.regSrc1 + io.imm
}
