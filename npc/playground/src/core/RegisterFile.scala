package core

import chisel3._
import chisel3.util._

class RegisterFile extends Module {
  val io = IO(new Bundle {
    val writeEnable = Input(Bool())
    val rs1Addr     = Input(UInt(5.W))
    val rs2Addr     = Input(UInt(5.W))
    val rdAddr      = Input(UInt(5.W))
    val writeData   = Input(UInt(32.W))
    val rs1Data     = Output(UInt(32.W))
    val rs2Data     = Output(UInt(32.W))
  })

  val regfile = RegInit(VecInit(Seq.fill(32)(0.U(32.W))))

  // Read ports
  io.rs1Data := Mux(io.rs1Addr.orR, regfile(io.rs1Addr), 0.U)
  io.rs2Data := Mux(io.rs2Addr.orR, regfile(io.rs2Addr), 0.U)

  // Write port
  when(io.writeEnable && io.rdAddr.orR) {
    regfile(io.rdAddr) := io.writeData
  }
}
