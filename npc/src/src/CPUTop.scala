package core

import chisel3._
import chisel3.util._
import debug.DebugBundle
import core.IF._
import core.ID._
import core.EXE._
import core.MEM._
import core.WB._

class CPUTop extends Module {
  val io = IO(new Bundle {
    val debug = new DebugBundle
  })

}
