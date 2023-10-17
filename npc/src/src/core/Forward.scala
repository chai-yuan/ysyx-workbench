package core

import chisel3._
import chisel3.util._
import core.MEM.MEM2ForwardBundle
import core.WB.WB2ForwardBundle
import core.EXE.EXE2ForwardBundle

class Forward2EXEBundle extends Bundle {
  val forward1Sel = Output(Bool())
  val regData1    = Output(UInt(32.W))
  val forward2Sel = Output(Bool())
  val regData2    = Output(UInt(32.W))
}

class Forward extends Module {
  val io = IO(new Bundle {
    val forward2exe = new Forward2EXEBundle
    val exe2forward = Flipped(new EXE2ForwardBundle)
    val mem2forward = Flipped(new MEM2ForwardBundle)
    val wb2forward  = Flipped(new WB2ForwardBundle)
  })

  // forward2exe
  val forward1 = ((io.exe2forward.regSrc1 === io.mem2forward.addr) && (io.mem2forward.enable)) ||
    ((io.exe2forward.regSrc1 === io.wb2forward.addr) && (io.wb2forward.enable))
  val forward2 = ((io.exe2forward.regSrc2 === io.mem2forward.addr) && (io.mem2forward.enable)) ||
    ((io.exe2forward.regSrc2 === io.wb2forward.addr) && (io.wb2forward.enable))

  io.forward2exe.forward1Sel := forward1
  io.forward2exe.forward2Sel := forward2

  io.forward2exe.regData1 := MuxCase(
    0.U,
    Seq(
      ((io.exe2forward.regSrc1 === io.mem2forward.addr) && io.mem2forward.enable) -> (io.mem2forward.data),
      ((io.exe2forward.regSrc1 === io.wb2forward.addr) && io.wb2forward.enable) -> (io.wb2forward.data)
    )
  )
  io.forward2exe.regData2 := MuxCase(
    0.U,
    Seq(
      ((io.exe2forward.regSrc2 === io.mem2forward.addr) && io.mem2forward.enable) -> (io.mem2forward.data),
      ((io.exe2forward.regSrc2 === io.wb2forward.addr) && io.wb2forward.enable) -> (io.wb2forward.data)
    )
  )
}
