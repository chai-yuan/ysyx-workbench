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

//   io.out.ar <> io.instIn.ar
//   io.out.r <> io.instIn.r
  // 指令读优先
  val inst  = io.instIn
  val data  = io.dataIn
  val out   = io.out
  val grant = RegInit(1.U(2.W))
  grant := MuxCase(
    grant,
    Seq(
      (data.ar.valid && out.ar.ready) -> ("b10".U),
      (inst.ar.valid && out.ar.ready) -> ("b01".U)
    )
  )

  out.ar.valid := (Cat(data.ar.valid, inst.ar.valid) & grant).orR
  out.ar.addr := MuxCase(
    0.U,
    Seq(
      (grant === "b01".U) -> (inst.ar.addr),
      (grant === "b10".U) -> (data.ar.addr)
    )
  )
  inst.ar.ready := Mux(grant === "b01".U, out.ar.ready, false.B)
  data.ar.ready := Mux(grant === "b10".U, out.ar.ready, false.B)

  inst.r.valid := Mux(grant === "b01".U, out.r.valid, false.B)
  inst.r.data  := Mux(grant === "b01".U, out.r.data, 0.U)
  inst.r.resp  := Mux(grant === "b01".U, out.r.resp, 0.U)
  data.r.valid := Mux(grant === "b10".U, out.r.valid, false.B)
  data.r.data  := Mux(grant === "b10".U, out.r.data, 0.U)
  data.r.resp  := Mux(grant === "b10".U, out.r.resp, 0.U)
  out.r.ready := MuxCase(
    false.B,
    Seq(
      (grant === "b01".U) -> (inst.r.ready),
      (grant === "b10".U) -> (data.r.ready)
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
