package io

import chisel3._
import chisel3.util._

class PipelineStageIO extends Bundle {
  def default() = 0.U.asTypeOf(this)
}
