package core

import chisel3._
import chisel3.util._

import config.Configs._

class PCRegIO extends Bundle {
  val addrOut      = Output(UInt(32.W)) // 地址输出
  val ctrlJump     = Input(Bool()) // 当前指令是否为跳转指令
  val ctrlBranch   = Input(Bool()) // 当前指令是否为分支指令
  val resultBranch = Input(Bool()) // 分支结果是否为分支成功
  val addrTarget   = Input(UInt(32.W)) // 跳转/分支的目的地址
}

class PCReg extends Module {
  val io = IO(new PCRegIO()) // 输入输出接口

  val regPC = RegInit(UInt(32.W), START_ADDR.U) // PC寄存器，初始化时重置为START_ADDR

  when(io.ctrlJump || (io.ctrlBranch && io.resultBranch)) { // 跳转或分支成功时，更新为目的地址
    regPC := io.addrTarget
  }.otherwise {
    regPC := regPC + 4.U
  }

  io.addrOut := regPC
}
