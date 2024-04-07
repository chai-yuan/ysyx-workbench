package core.pipeline

import chisel3._
import chisel3.util._
import config.CPUconfig._
import core.define.InstructionDefine._
import core.define.OperationDefine._
import core.define.ControlSignDefine._
import io._

class DecodeStage extends Module {
  val io = IO(new Bundle {
    val if2id   = Input(new IF2IDIO)
    val control = new DecodeStageControlIO
    val id2exe  = Output(new ID2EXEIO)

    val branchInfo = Output(new BranchInfoIO(5))
    val read       = new SimpleInIO(INST_WIDTH)
    val regRead1   = new RegReadIO
    val regRead2   = new RegReadIO
  })
  val if2id = io.if2id.IF

  val stallDelay = RegNext(io.control.stall)
  val lastInst   = Reg(UInt(INST_WIDTH.W))
  when(!stallDelay) { lastInst := io.read.rdata }
  val inst = MuxCase(
    io.read.rdata,
    Seq(
      (!if2id.instValid) -> (NOP),
      (stallDelay) -> (lastInst)
    )
  )
  // 译码
  val rd  = inst(11, 7)
  val rs1 = inst(19, 15)
  val rs2 = inst(24, 20)

  val immI = inst(31, 20)
  val immS = Cat(inst(31, 25), inst(11, 7))
  val immB = Cat(inst(31), inst(7), inst(30, 25), inst(11, 8), 0.U(1.W))
  val immU = Cat(inst(31, 12), 0.U(12.W))
  val immJ = Cat(inst(31), inst(19, 12), inst(20), inst(30, 21), 0.U(1.W))

  val ((regEn1: Bool) :: (regEn2: Bool) :: (regWen: Bool) ::
    aluSrc1Op :: aluSrc2Op :: aluOp :: branchFlag :: lsuOp :: csrOp ::
    mduOp :: excType :: Nil) = ListLookup(inst, DEFAULT, TABLE)

  // 生成ALU输入
  def generateOpr(oprSel: UInt) = MuxLookup(oprSel, 0.S)(
    Seq(
      OPR_REG1 -> io.regRead1.data.asSInt,
      OPR_REG2 -> io.regRead2.data.asSInt,
      OPR_IMMI -> immI.asSInt,
      OPR_IMMS -> immS.asSInt,
      OPR_IMMU -> immU.asSInt,
      OPR_IMMR -> rs2.zext,
      OPR_PC -> if2id.pc.asSInt,
      OPR_4 -> 4.S
    )
  )
  // 生成跳转信号
  val branchTaken = MuxLookup(branchFlag, false.B)(
    Seq(
      BR_AL -> true.B,
      BR_EQ -> (io.regRead1.data === io.regRead2.data),
      BR_NE -> (io.regRead1.data =/= io.regRead2.data),
      BR_LT -> (io.regRead1.data.asSInt < io.regRead2.data.asSInt),
      BR_GE -> (io.regRead1.data.asSInt >= io.regRead2.data.asSInt),
      BR_LTU -> (io.regRead1.data < io.regRead2.data),
      BR_GEU -> (io.regRead1.data >= io.regRead2.data)
    )
  )
  val targetJAL    = (if2id.pc.asSInt + immJ.asSInt).asUInt
  val targetJALR   = Cat((io.regRead1.data.asSInt + immI.asSInt)(ADDR_WIDTH - 1, 1), 0.U)
  val targetJ      = Mux(regEn1, targetJALR, targetJAL)
  val targetB      = (if2id.pc.asSInt + immB.asSInt).asUInt
  val branchTarget = Mux(branchFlag === BR_AL, targetJ, targetB)
  val flushPC      = Mux(branchTaken, branchTarget, if2id.pc + 4.U)
  val branchMiss   = if2id.predTaken =/= branchTaken || (branchTaken && if2id.predTarget =/= branchTarget)

  // CSR 信号
  val csrOperation = MuxLookup(csrOp, CSR_NOP)(
    Seq(
      CSR_RW -> Mux(rd === 0.U, CSR_W, CSR_RW),
      CSR_RS -> Mux(rs1 === 0.U, CSR_R, CSR_RS),
      CSR_RC -> Mux(rs1 === 0.U, CSR_R, CSR_RC)
    )
  )
  val csrWriteData = Mux(csrOperation === CSR_NOP, 0.U, Mux(regEn1, io.regRead1.data, rs1))
  // 流水线控制
  io.control.flushIF := !io.control.stall && branchMiss
  io.control.flushPc := flushPC
  // 分支信息(用于分支预测)
  io.branchInfo.branch := branchFlag =/= BR_NOP
  io.branchInfo.jump   := branchFlag === BR_AL
  io.branchInfo.taken  := branchTaken
  io.branchInfo.index  := if2id.predIndex
  io.branchInfo.pc     := if2id.pc
  io.branchInfo.target := branchTarget
  // 寄存器
  io.regRead1.en   := regEn1
  io.regRead2.en   := regEn2
  io.regRead1.addr := rs1
  io.regRead2.addr := rs2
  // 下一级流水线
  io.id2exe.IF <> if2id
  io.id2exe.ID.inst         := inst // 指令和下一个nextPC
  io.id2exe.ID.nextpc       := flushPC
  io.id2exe.ID.aluOp        := aluOp
  io.id2exe.ID.mduOp        := mduOp
  io.id2exe.ID.src1         := generateOpr(aluSrc1Op).asUInt
  io.id2exe.ID.src2         := generateOpr(aluSrc2Op).asUInt
  io.id2exe.ID.lsuOp        := lsuOp
  io.id2exe.ID.lsuData      := io.regRead2.data
  io.id2exe.ID.regWen       := regWen
  io.id2exe.ID.regWaddr     := rd
  io.id2exe.ID.csrOp        := csrOperation
  io.id2exe.ID.csrAddr      := immI
  io.id2exe.ID.csrWriteData := csrWriteData
  io.id2exe.ID.exceptType   := excType
}

class DecodeStageControlIO extends Bundle {
  val stall = Input(Bool())

  val flushIF = Output(Bool())
  val flushPc = Output(UInt(ADDR_WIDTH.W))
}
