package bus

import chisel3._
import chisel3.util._
import config.CPUconfig._
import io._

class AXIliteXbar extends Module {
  val io = IO(new Bundle {
    val in   = new AXIliteMasterIO(ADDR_WIDTH, DATA_WIDTH)
    val out0 = Flipped(new AXIliteMasterIO(ADDR_WIDTH, DATA_WIDTH))
    val out1 = Flipped(new AXIliteMasterIO(ADDR_WIDTH, DATA_WIDTH))
  })

  


}
