package config

import chisel3._
import chisel3.util._

/**
  * 用于调整生成处理器的参数
  */
object CPUconfig{
  val NAME = "CRRV4"

  val INST_WIDTH = 32
  val ADDR_WIDTH = 32
  val DATA_WIDTH = 32
  
  val REG_COUNT  = 32

  val RESET_PC = "h8000_0000".U(ADDR_WIDTH.W)
}