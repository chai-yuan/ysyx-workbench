package core.ID

import chisel3._
import chisel3.util._
import config.Config
import core.IF.IF2IDBundle

class ID2Hazerd extends Bundle {
  val inst     = Output(UInt(32.W))
  val regData1 = Output(UInt(32.W))
  val regData2 = Output(UInt(32.W))
}

class IDBundle extends Bundle {
  val if2id     = Flipped(new IF2IDBundle)
  val id2hazerd = new ID2Hazerd
  val id2exe    = new ID2EXEBundle
}

class ID extends Module {
  val io = IO(new IDBundle)

}
