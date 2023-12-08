package core.pipeline

import chisel3._
import chisel3.util._

/**
  * 冒险处理单元
  * 用于处理寄存器和CSR的前递，以及在冲突时向控制模块发送暂停信号
  */
class HazardResolver extends Module{
}