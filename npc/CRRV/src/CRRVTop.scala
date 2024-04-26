package core

import chisel3._
import chisel3.util._
import config.CPUconfig._
import io._
import sim.Debug
import bus._
import sim.SimMemory

class CRRVTop extends Module {
  if (targetGen == TargetGen.SOC) {
    val io = IO(new CPUWarpIO)
    // CRRV CPU
    val core = Module(new Core)
    // val arbiter    = Module(new SimpleArbiter)
    val axiArbiter      = Module(new AXI4Arbiter)
    val xbar            = Module(new SimpleXbar)
    val clint           = Module(new CLINT)
    val simpleAlignment = Module(new SimpleAlignment)
    val simple2axiInst  = Module(new Simple2AXI4)
    val simple2axiData  = Module(new Simple2AXI4)
    val debug           = Module(new Debug)
    // val icache         = Module(new InstCache(256, 2))
    // icache.io.flush := false.B

    core.io.inst <> simple2axiInst.io.simple
    core.io.data <> simpleAlignment.io.in
    simpleAlignment.io.out <> xbar.io.simpleIn
    xbar.io.simpleOut <> simple2axiData.io.simple
    xbar.io.simpleCLINT <> clint.io.core

    axiArbiter.io.axiInst <> simple2axiInst.io.axi
    axiArbiter.io.axiData <> simple2axiData.io.axi
    val aximaster = axiArbiter.io.axiOut

    // CPU IO
    core.io.intr.timer  := clint.io.intr
    core.io.intr.soft   := false.B
    core.io.intr.extern := false.B
    debug.io.clock      := clock
    debug.io.reset      := reset
    core.io.debug <> debug.io.debug

    aximaster.aw.ready := io.master.awready
    io.master.awvalid  := aximaster.aw.valid
    io.master.awaddr   := aximaster.aw.bits.addr
    io.master.awid     := aximaster.aw.bits.id
    io.master.awlen    := aximaster.aw.bits.len
    io.master.awsize   := aximaster.aw.bits.size
    io.master.awburst  := aximaster.aw.bits.burst

    aximaster.w.ready := io.master.wready
    io.master.wvalid  := aximaster.w.valid
    io.master.wdata   := aximaster.w.bits.data
    io.master.wstrb   := aximaster.w.bits.strb
    io.master.wlast   := aximaster.w.bits.last

    io.master.bready      := aximaster.b.ready
    aximaster.b.valid     := io.master.bvalid
    aximaster.b.bits.resp := io.master.bresp
    aximaster.b.bits.id   := io.master.bid

    aximaster.ar.ready := io.master.arready
    io.master.arvalid  := aximaster.ar.valid
    io.master.araddr   := aximaster.ar.bits.addr
    io.master.arid     := aximaster.ar.bits.id
    io.master.arlen    := aximaster.ar.bits.len
    io.master.arsize   := aximaster.ar.bits.size
    io.master.arburst  := aximaster.ar.bits.burst

    io.master.rready      := aximaster.r.ready
    aximaster.r.valid     := io.master.rvalid
    aximaster.r.bits.resp := io.master.rresp
    aximaster.r.bits.data := io.master.rdata
    aximaster.r.bits.last := io.master.rlast
    aximaster.r.bits.id   := io.master.rid

    io.slave.awready := false.B
    io.slave.wready  := false.B
    io.slave.bvalid  := false.B
    io.slave.bresp   := 0.U
    io.slave.bid     := 0.U
    io.slave.arready := false.B
    io.slave.rvalid  := false.B
    io.slave.rresp   := 0.U
    io.slave.rdata   := 0.U
    io.slave.rlast   := false.B
    io.slave.rid     := 0.U
  } else if (targetGen == TargetGen.NPC) {
    val io = IO(new Bundle {})
    // CRRV CPU
    val core            = Module(new Core)
    val arbiter         = Module(new SimpleArbiter)
    val xbar            = Module(new SimpleXbar)
    val clint           = Module(new CLINT)
    val simpleAlignment = Module(new SimpleAlignment)
    val debug           = Module(new Debug)

    val mem = Module(new SimMemory)

    core.io.inst <> arbiter.io.simpleInst
    core.io.data <> simpleAlignment.io.in
    simpleAlignment.io.out <> xbar.io.simpleIn
    xbar.io.simpleOut <> arbiter.io.simpleData
    xbar.io.simpleCLINT <> clint.io.core

    // CPU IO
    core.io.intr.timer  := clint.io.intr
    core.io.intr.soft   := false.B
    core.io.intr.extern := false.B

    debug.io.clock := clock
    debug.io.reset := reset
    core.io.debug <> debug.io.debug

    mem.io.clock := clock
    mem.io.reset := reset
    mem.io.simple <> arbiter.io.simpleOut
  }
}
