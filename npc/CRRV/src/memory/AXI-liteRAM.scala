package memory

import chisel3._
import chisel3.util._
import chisel3.util.random.LFSR
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

class AXIliteRAM(randomDelayEnable: Boolean, randomSeed: BigInt = 1) extends Module {
  val io = IO(Flipped(new AXIliteBundle))

  val randomDelay = Wire(UInt(4.W))
  if (randomDelayEnable) {
    randomDelay := LFSR(4, true.B, Some(randomSeed))
  } else {
    randomDelay := 0.U
  }

  val sram = Module(new SRAM)

  // read
  val rvalid   = Wire(Bool())
  val rready   = Wire(Bool())
  val raddrReg = RegInit(0.U(32.W))

  val arvalid = io.ar.valid
  val arready = ((raddrReg === 0.U) || (rvalid && rready)) && !randomDelay(0) // 可以添加延迟
  raddrReg := MuxCase(
    raddrReg,
    Seq(
      (arvalid && arready) -> (io.ar.addr),
      (rvalid && rready) -> (0.U)
    )
  )
  val raddr = Mux(arvalid && arready, io.ar.addr, raddrReg) // 获得地址

  rvalid := RegNext(raddr =/= 0.U) && !randomDelay(1) // 当收到有效地址的一周期后，便可以返回数据
  rready := io.r.ready
  val rdata = sram.io.rdata

  io.ar.ready := arready
  io.r.valid  := rvalid
  io.r.data   := rdata
  io.r.resp   := 0.U // 不会发生异常

  sram.io.ren   := raddr =/= 0.U
  sram.io.raddr := raddr

  // write
  val awvalid  = io.aw.valid
  val wvalid   = io.w.valid
  val waddrReg = RegInit(0.U(32.W))
  val wdataReg = RegInit(0.U(33.W)) // 多添加1个有效位
  val wstrbReg = RegInit(0.U(4.W))
  val awready  = awvalid && !randomDelay(2) // 随时准备就绪，可以添加随机延迟
  val wready   = wvalid && !randomDelay(2)

  waddrReg := MuxCase(
    waddrReg,
    Seq(
      (awvalid && awready) -> (io.aw.addr),
      (io.b.valid && io.b.ready) -> (0.U)
    )
  )
  wdataReg := MuxCase(
    wdataReg,
    Seq(
      (wvalid && wready) -> (Cat(1.U(1.W), io.w.data)),
      (io.b.valid && io.b.ready) -> (0.U)
    )
  )
  wstrbReg := MuxCase(
    wstrbReg,
    Seq(
      (wvalid && wready) -> (io.w.strb),
      (io.b.valid && io.b.ready) -> (0.U)
    )
  )
  val waddr = Mux(awvalid && awready, io.aw.addr, waddrReg)
  val wdata = Mux(wvalid && wready, io.w.data, wdataReg)
  val wstrb = Mux(wvalid && wready, io.w.strb, wstrbReg)

  io.aw.ready := awready
  io.w.ready  := wready
  io.b.valid  := true.B // 不会发生异常
  io.b.resp   := 0.U

  sram.io.wen   := waddr =/= 0.U
  sram.io.waddr := waddr
  sram.io.wdata := wdata
  sram.io.wmask := wstrb
}
