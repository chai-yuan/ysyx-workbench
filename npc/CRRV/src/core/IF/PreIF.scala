package core.IF

import chisel3._
import chisel3.util._
import core.ID.BranchBundle
import memory.AddrBundle
import ujson.False

class PreIF extends Module {
  val io = IO(new Bundle {
    val instMem  = new AddrBundle
    val preif2if = Decoupled(new PreIF2IFBundle)
    val pc       = Input(UInt(32.W))
    val branch   = Flipped(new BranchBundle)
  })
  val arvalid = io.preif2if.ready
  val arready = io.instMem.ready

  // pipeline ctrl
  val readyGo   = arvalid && arready // 地址发送成功
  val ifValid   = readyGo
  val ifAllowin = io.preif2if.ready
  io.preif2if.valid := ifValid

  // nextPC
  val pc = io.pc
  val nextPC = MuxCase(
    pc + 4.U,
    Seq(
      (io.branch.branchSel) -> (io.branch.branchTarget)
    )
  )

  // instmem
  val nextPCReg = RegInit(0.U(32.W))
  nextPCReg := MuxCase(
    nextPCReg,
    Seq(
      // 握手失败，将地址储存下来
      (arvalid && !arready && nextPCReg === 0.U) -> (nextPC),
      // 握手成功，清空地址缓存
      (arvalid && arready) -> (0.U(32.W))
    )
  )
  val raddr = Mux(nextPCReg === 0.U, nextPC, nextPCReg) // 获得应该发送的地址
  io.instMem.valid := arvalid
  io.instMem.addr  := raddr

  // to if data
  io.preif2if.bits.nextPC   := raddr
}
