package memory

import chisel3._
import chisel3.util._
import config.GenConfig
import scala.reflect.internal.Mode

class AddrBundle extends Bundle {
  val valid = Output(Bool())
  val ready = Input(Bool())
  val addr  = Output(UInt(32.W))
}

class ReadBundle extends Bundle {
  val valid = Input(Bool())
  val ready = Output(Bool())
  val data  = Input(UInt(32.W))
  val resp  = Input(UInt(2.W))
}

class WriteBundle extends Bundle {
  val valid = Output(Bool())
  val ready = Input(Bool())
  val data  = Output(UInt(32.W))
  val strb  = Output(UInt(4.W))
}

class WriteBackBundle extends Bundle {
  val valid = Input(Bool())
  val ready = Output(Bool())
  val resp  = Input(UInt(2.W))
}

class AXIliteBundle extends Bundle {
  val ar = new AddrBundle
  val r  = new ReadBundle
  val aw = new AddrBundle
  val w  = new WriteBundle
  val b  = new WriteBackBundle
}

class AXIliteRAM extends Module {
  val io = IO(Flipped(new AXIliteBundle))

  val sram = Module(new SRAM)

  // read
  val rvalid = Wire(Bool())
  val rready = Wire(Bool())

  val arvalid  = io.ar.valid
  val arready  = !(rvalid && !rready) // 如果发送数据还没有被读取，就不接受新地址，可以添加随机延迟
  val raddrReg = RegInit(0.U(32.W))
  raddrReg := Mux(arvalid && arready, io.ar.addr, raddrReg)
  val raddr = Mux(arvalid && arready, io.ar.addr, raddrReg) // 获得地址

  rvalid := RegNext(raddr =/= 0.U) // 当收到有效地址的一周期后，便可以返回数据，可以添加延迟
  rready := io.r.ready
  val rdata = sram.io.rdata

  io.ar.ready := arready
  io.r.valid  := rvalid
  io.r.data   := rdata
  io.r.resp   := 0.U // 不会发生异常

  sram.io.ren   := raddr =/= 0.U
  sram.io.raddr := raddr

  // write 待完善不同时发送地址和数据
  val awvalid = io.aw.valid
  val wvalid  = io.w.valid
  val awready = awvalid // 随时准备就绪，可以添加随机延迟
  val wready  = wvalid

  val waddr = Mux(awvalid && awready, io.aw.addr, 0.U)
  val wdata = Mux(wvalid && wready, io.w.data, 0.U)
  val wstrb = Mux(wvalid && wready, io.w.strb, 0.U)

  io.aw.ready := awready
  io.w.ready  := wready
  io.b.valid  := true.B // 不会发生异常
  io.b.resp   := 0.U

  sram.io.wen   := waddr =/= 0.U
  sram.io.waddr := waddr
  sram.io.wdata := wdata
  sram.io.wmask := wstrb
}
