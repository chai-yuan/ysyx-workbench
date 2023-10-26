package core.MEM

import chisel3._
import chisel3.util._
import core.WB._

class MEM2GlobalBundle extends Bundle {
  val forward = new WriteBackBundle
}
