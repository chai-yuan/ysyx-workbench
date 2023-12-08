package core.pipeline

import chisel3._
import chisel3.util._

/**
  * 流水线控制模块
  * 接受来自各个模块的信号，生成各级流水线的暂停和冲刷信号
  */
class PipelineControl extends Module {}
