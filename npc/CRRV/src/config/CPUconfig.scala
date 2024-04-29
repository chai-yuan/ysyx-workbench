package config

import chisel3._
import chisel3.util._
import chisel3.util.Enum

/**
  * 用于调整生成处理器的参数
  */
object CPUconfig {
  val NAME = "CRRV4"

  // 基本信息
  val INST_WIDTH = 32
  val ADDR_WIDTH = 32
  val DATA_WIDTH = 32
  val REG_COUNT  = 32
  val RESET_PC   = "h8000_0000".U(ADDR_WIDTH.W)

  // 生成目标
  val GEN_DEBUG = true

  // 定义生成目标枚举
  object TargetGen extends ChiselEnum {
    val SOC, NPC = Value
  }

  // 选择生成的目标
  val targetGen: TargetGen.Type = TargetGen.NPC
}
