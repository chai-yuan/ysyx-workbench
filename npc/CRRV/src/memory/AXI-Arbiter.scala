package memory

import chisel3._
import chisel3.util._
import os.truncate

class AXI_Arbiter extends Module {
  val io = IO(new Bundle {
    val instIn = Flipped(new AXIliteBundle)
    val dataIn = Flipped(new AXIliteBundle)
    val out    = new AXIliteBundle
  })
  // 循环访问
  val inst  = io.instIn
  val data  = io.dataIn
  val out   = io.out
  val state = RegInit(0.U(2.W)) // instaddr instread dataaddr dataread
  state := MuxCase(
    state,
    Seq(
      (state === 0.U && !inst.ar.valid) -> (2.U),
      (state === 2.U && !data.ar.valid) -> (0.U),
      (state === 0.U && (inst.ar.valid && inst.ar.ready)) -> (1.U),
      (state === 1.U && (inst.r.valid && inst.r.ready)) -> (2.U),
      (state === 2.U && (data.ar.valid && data.ar.ready)) -> (3.U),
      (state === 3.U && (data.r.valid && data.r.ready)) -> (0.U)
    )
  )

  out.ar.valid := MuxCase(
    false.B,
    Seq(
      (state === 0.U) -> (inst.ar.valid),
      (state === 2.U) -> (data.ar.valid)
    )
  )
  out.ar.addr := MuxCase(
    0.U,
    Seq(
      (state === 0.U) -> (inst.ar.addr),
      (state === 2.U) -> (data.ar.addr)
    )
  )
  inst.ar.ready := (state === 0.U) && out.ar.ready
  data.ar.ready := (state === 2.U) && out.ar.ready

  inst.r.valid := Mux(state === 1.U, out.r.valid, false.B)
  inst.r.data  := Mux(state === 1.U, out.r.data, 0.U)
  inst.r.resp  := Mux(state === 1.U, out.r.resp, 0.U)
  data.r.valid := Mux(state === 3.U, out.r.valid, false.B)
  data.r.data  := Mux(state === 3.U, out.r.data, 0.U)
  data.r.resp  := Mux(state === 3.U, out.r.resp, 0.U)
  out.r.ready := MuxCase(
    false.B,
    Seq(
      (state === 1.U) -> (inst.r.ready),
      (state === 3.U) -> (data.r.ready)
    )
  )

  // 只有dataIn会写入，而instIn不会
  out.aw <> data.aw
  out.w <> data.w
  out.b <> data.b
  inst.aw.ready := false.B
  inst.w.ready  := false.B
  inst.b.valid  := false.B
  inst.b.resp   := 0.U
}
