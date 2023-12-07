package core

import chisel3._
import chisel3.util._
import debug.DebugBundle
import memory.SRAM
import memory.AXIliteRAM
import memory.AXI_Arbiter

class CPUTop extends Module {
  val io = IO(new Bundle {
    val debug = new DebugBundle
  })

//   val core    = Module(new CoreTop)
//   val instRAM = Module(new AXIliteRAM(randomDelayEnable = true, randomSeed = 3))
//   val dataRAM = Module(new AXIliteRAM(randomDelayEnable = true, randomSeed = 2))

//   core.io.inst <> instRAM.io
//   core.io.data <> dataRAM.io

  val core    = Module(new CoreTop)
  val arbiter = Module(new AXI_Arbiter)
  val ram     = Module(new AXIliteRAM(randomDelayEnable = false, randomSeed = 1))

  core.io.inst <> arbiter.io.instIn
  core.io.data <> arbiter.io.dataIn
  arbiter.io.out <> ram.io

  core.io.debug <> io.debug
}
