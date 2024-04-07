package core.define

import chisel3._
import chisel3.util._
import core.define.InstructionDefine._
import core.define.OperationDefine._

object ControlSignDefine {
  val Y = true.B
  val N = false.B

    val DEFAULT = List(
        N,          // reg1 读取
        N,          // reg2 读取
        N,          // 寄存器写
        OPR_ZERO,   // ALU操作数1
        OPR_ZERO,   // ALU操作数2
        ALU_NOP,    // ALU操作符
        BR_NOP,     // 跳转条件
        LSU_NOP,    // 访存操作符
        CSR_NOP,    // CSR操作符
        MDU_NOP,    // 乘除法操作符
        EXC_ILLEG   // 指令异常状态
        )

  val TABLE = Array(
    ADD       ->  List(Y, Y, Y, OPR_REG1, OPR_REG2, ALU_ADD,  BR_NOP,   LSU_NOP,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    ADDI      ->  List(Y, N, Y, OPR_REG1, OPR_IMMI, ALU_ADD,  BR_NOP,   LSU_NOP,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    SUB       ->  List(Y, Y, Y, OPR_REG1, OPR_REG2, ALU_SUB,  BR_NOP,   LSU_NOP,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    LUI       ->  List(N, N, Y, OPR_ZERO, OPR_IMMU, ALU_OR,   BR_NOP,   LSU_NOP,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    AUIPC     ->  List(N, N, Y, OPR_PC,   OPR_IMMU, ALU_ADD,  BR_NOP,   LSU_NOP,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    XOR       ->  List(Y, Y, Y, OPR_REG1, OPR_REG2, ALU_XOR,  BR_NOP,   LSU_NOP,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    XORI      ->  List(Y, N, Y, OPR_REG1, OPR_IMMI, ALU_XOR,  BR_NOP,   LSU_NOP,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    OR        ->  List(Y, Y, Y, OPR_REG1, OPR_REG2, ALU_OR,   BR_NOP,   LSU_NOP,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    ORI       ->  List(Y, N, Y, OPR_REG1, OPR_IMMI, ALU_OR,   BR_NOP,   LSU_NOP,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    AND       ->  List(Y, Y, Y, OPR_REG1, OPR_REG2, ALU_AND,  BR_NOP,   LSU_NOP,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    ANDI      ->  List(Y, N, Y, OPR_REG1, OPR_IMMI, ALU_AND,  BR_NOP,   LSU_NOP,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    SLT       ->  List(Y, Y, Y, OPR_REG1, OPR_REG2, ALU_SLT,  BR_NOP,   LSU_NOP,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    SLTI      ->  List(Y, N, Y, OPR_REG1, OPR_IMMI, ALU_SLT,  BR_NOP,   LSU_NOP,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    SLTU      ->  List(Y, Y, Y, OPR_REG1, OPR_REG2, ALU_SLTU, BR_NOP,   LSU_NOP,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    SLTIU     ->  List(Y, N, Y, OPR_REG1, OPR_IMMI, ALU_SLTU, BR_NOP,   LSU_NOP,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    SLL       ->  List(Y, Y, Y, OPR_REG1, OPR_REG2, ALU_SLL,  BR_NOP,   LSU_NOP,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    SLLI      ->  List(Y, N, Y, OPR_REG1, OPR_IMMR, ALU_SLL,  BR_NOP,   LSU_NOP,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    SRL       ->  List(Y, Y, Y, OPR_REG1, OPR_REG2, ALU_SRL,  BR_NOP,   LSU_NOP,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    SRLI      ->  List(Y, N, Y, OPR_REG1, OPR_IMMR, ALU_SRL,  BR_NOP,   LSU_NOP,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    SRA       ->  List(Y, Y, Y, OPR_REG1, OPR_REG2, ALU_SRA,  BR_NOP,   LSU_NOP,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    SRAI      ->  List(Y, N, Y, OPR_REG1, OPR_IMMR, ALU_SRA,  BR_NOP,   LSU_NOP,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    BEQ       ->  List(Y, Y, N, OPR_ZERO, OPR_ZERO, ALU_ADD,  BR_EQ,    LSU_NOP,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    BNE       ->  List(Y, Y, N, OPR_ZERO, OPR_ZERO, ALU_ADD,  BR_NE,    LSU_NOP,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    BLT       ->  List(Y, Y, N, OPR_ZERO, OPR_ZERO, ALU_ADD,  BR_LT,    LSU_NOP,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    BGE       ->  List(Y, Y, N, OPR_ZERO, OPR_ZERO, ALU_ADD,  BR_GE,    LSU_NOP,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    BLTU      ->  List(Y, Y, N, OPR_ZERO, OPR_ZERO, ALU_ADD,  BR_LTU,   LSU_NOP,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    BGEU      ->  List(Y, Y, N, OPR_ZERO, OPR_ZERO, ALU_ADD,  BR_GEU,   LSU_NOP,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    JAL       ->  List(N, N, Y, OPR_PC,   OPR_4,    ALU_ADD,  BR_AL,    LSU_NOP,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    JALR      ->  List(Y, N, Y, OPR_PC,   OPR_4,    ALU_ADD,  BR_AL,    LSU_NOP,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    LB        ->  List(Y, N, Y, OPR_REG1, OPR_IMMI, ALU_ADD,  BR_NOP,   LSU_LB,   CSR_NOP,  MDU_NOP,    EXC_NONE),
    LH        ->  List(Y, N, Y, OPR_REG1, OPR_IMMI, ALU_ADD,  BR_NOP,   LSU_LH,   CSR_NOP,  MDU_NOP,    EXC_NONE),
    LW        ->  List(Y, N, Y, OPR_REG1, OPR_IMMI, ALU_ADD,  BR_NOP,   LSU_LW,   CSR_NOP,  MDU_NOP,    EXC_NONE),
    LBU       ->  List(Y, N, Y, OPR_REG1, OPR_IMMI, ALU_ADD,  BR_NOP,   LSU_LBU,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    LHU       ->  List(Y, N, Y, OPR_REG1, OPR_IMMI, ALU_ADD,  BR_NOP,   LSU_LHU,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    SB        ->  List(Y, Y, N, OPR_REG1, OPR_IMMS, ALU_ADD,  BR_NOP,   LSU_SB,   CSR_NOP,  MDU_NOP,    EXC_NONE),
    SH        ->  List(Y, Y, N, OPR_REG1, OPR_IMMS, ALU_ADD,  BR_NOP,   LSU_SH,   CSR_NOP,  MDU_NOP,    EXC_NONE),
    SW        ->  List(Y, Y, N, OPR_REG1, OPR_IMMS, ALU_ADD,  BR_NOP,   LSU_SW,   CSR_NOP,  MDU_NOP,    EXC_NONE),

    CSRRW     ->  List(Y, N, Y, OPR_ZERO, OPR_ZERO, ALU_NOP,  BR_NOP,   LSU_NOP,  CSR_RW,   MDU_NOP,    EXC_NONE),
    CSRRS     ->  List(Y, N, Y, OPR_ZERO, OPR_ZERO, ALU_NOP,  BR_NOP,   LSU_NOP,  CSR_RS,   MDU_NOP,    EXC_NONE),
    CSRRC     ->  List(Y, N, Y, OPR_ZERO, OPR_ZERO, ALU_NOP,  BR_NOP,   LSU_NOP,  CSR_RC,   MDU_NOP,    EXC_NONE),
    CSRRWI    ->  List(N, N, Y, OPR_ZERO, OPR_ZERO, ALU_NOP,  BR_NOP,   LSU_NOP,  CSR_RW,   MDU_NOP,    EXC_NONE),
    CSRRSI    ->  List(N, N, Y, OPR_ZERO, OPR_ZERO, ALU_NOP,  BR_NOP,   LSU_NOP,  CSR_RS,   MDU_NOP,    EXC_NONE),
    CSRRCI    ->  List(N, N, Y, OPR_ZERO, OPR_ZERO, ALU_NOP,  BR_NOP,   LSU_NOP,  CSR_RC,   MDU_NOP,    EXC_NONE),
    
    MUL       ->  List(Y, Y, Y, OPR_REG1, OPR_REG2, ALU_NOP,  BR_NOP,   LSU_NOP,  CSR_NOP,  MDU_MUL,    EXC_NONE),
    MULH      ->  List(Y, Y, Y, OPR_REG1, OPR_REG2, ALU_NOP,  BR_NOP,   LSU_NOP,  CSR_NOP,  MDU_MULH,   EXC_NONE),
    MULHSU    ->  List(Y, Y, Y, OPR_REG1, OPR_REG2, ALU_NOP,  BR_NOP,   LSU_NOP,  CSR_NOP,  MDU_MULHSU, EXC_NONE),
    MULHU     ->  List(Y, Y, Y, OPR_REG1, OPR_REG2, ALU_NOP,  BR_NOP,   LSU_NOP,  CSR_NOP,  MDU_MULHU,  EXC_NONE),
    DIV       ->  List(Y, Y, Y, OPR_REG1, OPR_REG2, ALU_NOP,  BR_NOP,   LSU_NOP,  CSR_NOP,  MDU_DIV,    EXC_NONE),
    DIVU      ->  List(Y, Y, Y, OPR_REG1, OPR_REG2, ALU_NOP,  BR_NOP,   LSU_NOP,  CSR_NOP,  MDU_DIVU,   EXC_NONE),
    REM       ->  List(Y, Y, Y, OPR_REG1, OPR_REG2, ALU_NOP,  BR_NOP,   LSU_NOP,  CSR_NOP,  MDU_REM,    EXC_NONE),
    REMU      ->  List(Y, Y, Y, OPR_REG1, OPR_REG2, ALU_NOP,  BR_NOP,   LSU_NOP,  CSR_NOP,  MDU_REMU,   EXC_NONE),

    LRW       ->  List(Y, N, Y, OPR_REG1, OPR_ZERO, ALU_OR,   BR_NOP,   LSU_LR,   CSR_NOP,  MDU_NOP,    EXC_NONE),
    SCW       ->  List(Y, Y, Y, OPR_REG1, OPR_ZERO, ALU_OR,   BR_NOP,   LSU_SC,   CSR_NOP,  MDU_NOP,    EXC_NONE),
    AMOSWAPW  ->  List(Y, Y, Y, OPR_REG1, OPR_ZERO, ALU_OR,   BR_NOP,   LSU_SWAP, CSR_NOP,  MDU_NOP,    EXC_NONE),
    AMOADDW   ->  List(Y, Y, Y, OPR_REG1, OPR_ZERO, ALU_OR,   BR_NOP,   LSU_ADD,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    AMOXORW   ->  List(Y, Y, Y, OPR_REG1, OPR_ZERO, ALU_OR,   BR_NOP,   LSU_XOR,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    AMOANDW   ->  List(Y, Y, Y, OPR_REG1, OPR_ZERO, ALU_OR,   BR_NOP,   LSU_AND,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    AMOORW    ->  List(Y, Y, Y, OPR_REG1, OPR_ZERO, ALU_OR,   BR_NOP,   LSU_OR,   CSR_NOP,  MDU_NOP,    EXC_NONE),
    AMOMINW   ->  List(Y, Y, Y, OPR_REG1, OPR_ZERO, ALU_OR,   BR_NOP,   LSU_MIN,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    AMOMAXW   ->  List(Y, Y, Y, OPR_REG1, OPR_ZERO, ALU_OR,   BR_NOP,   LSU_MAX,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    AMOMINUW  ->  List(Y, Y, Y, OPR_REG1, OPR_ZERO, ALU_OR,   BR_NOP,   LSU_MINU, CSR_NOP,  MDU_NOP,    EXC_NONE),
    AMOMAXUW  ->  List(Y, Y, Y, OPR_REG1, OPR_ZERO, ALU_OR,   BR_NOP,   LSU_MAXU, CSR_NOP,  MDU_NOP,    EXC_NONE),

    FENCE     ->  List(N, N, N, OPR_ZERO, OPR_ZERO, ALU_NOP,  BR_NOP,   LSU_FENC, CSR_NOP,  MDU_NOP,    EXC_NONE),
    FENCEI    ->  List(N, N, N, OPR_ZERO, OPR_ZERO, ALU_NOP,  BR_NOP,   LSU_FENI, CSR_NOP,  MDU_NOP,    EXC_NONE),

    ECALL     ->  List(N, N, N, OPR_ZERO, OPR_ZERO, ALU_ADD,  BR_NOP,   LSU_NOP,  CSR_NOP,  MDU_NOP,    EXC_ECALL),
    EBREAK    ->  List(N, N, N, OPR_ZERO, OPR_ZERO, ALU_ADD,  BR_NOP,   LSU_NOP,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    WFI       ->  List(N, N, N, OPR_ZERO, OPR_ZERO, ALU_NOP,  BR_NOP,   LSU_NOP,  CSR_NOP,  MDU_NOP,    EXC_NONE),
    MRET      ->  List(N, N, N, OPR_ZERO, OPR_ZERO, ALU_ADD,  BR_NOP,   LSU_NOP,  CSR_NOP,  MDU_NOP,    EXC_MRET),
  )
}
