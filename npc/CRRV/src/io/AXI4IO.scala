package io

import chisel3._
import chisel3.util._

abstract class AXI4Interface extends Bundle {
  def init() = this := 0.U.asTypeOf(this)
}

class AXI4AddrIO(val addrWidth: Int) extends AXI4Interface {
  val addr  = UInt(addrWidth.W)
  val id    = UInt(4.W)
  val len   = UInt(8.W)
  val size  = UInt(3.W)
  val burst = UInt(2.W)
}

class AXI4ReadIO(val dataWidth: Int) extends AXI4Interface {
  val data = UInt(dataWidth.W)
  val resp = UInt(2.W)
  val last = Bool()
  val id   = UInt(4.W)
}

class AXI4WriteIO(val dataWidth: Int) extends AXI4Interface {
  val data = UInt(dataWidth.W)
  val strb = UInt((dataWidth / 8).W)
  val last = Bool()
}

class AXI4WriteBackIO extends AXI4Interface {
  val resp  = UInt(2.W)
  val id    = UInt(4.W)
}

class AXI4MasterIO(val addrWidth: Int,val dataWidth : Int) extends AXI4Interface {
  val ar = Decoupled(new AXI4AddrIO(addrWidth))
  val r  = Flipped(Decoupled(new AXI4ReadIO(dataWidth)))
  val aw = Decoupled(new AXI4AddrIO(addrWidth))
  val w  = Decoupled(new AXI4WriteIO(dataWidth))
  val b  = Flipped(Decoupled(new AXI4WriteBackIO))

  override def init() = {
    ar.bits.init()
    ar.valid := false.B
    r.ready  := false.B
    aw.bits.init()
    aw.valid := false.B
    w.bits.init()
    w.valid := false.B
    b.ready := false.B
  }
}
