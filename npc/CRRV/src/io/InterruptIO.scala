package io

import chisel3._

class InterruptIO extends Bundle {
  val timer  = Input(Bool())
  val soft   = Input(Bool())
  val extern = Input(Bool())
}
