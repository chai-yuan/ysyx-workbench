/***************************************************************************************
 * Copyright (c) 2014-2022 Zihao Yu, Nanjing University
 *
 * NEMU is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 *
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 *
 * See the Mulan PSL v2 for more details.
 ***************************************************************************************/

#include <cpu/cpu.h>
#include <cpu/decode.h>
#include <cpu/ifetch.h>
#include <cpu/difftest.h>
#include <utils.h>
#include "local-include/reg.h"

#define R(i) gpr(i)
#define CSR(i) *isa_csr_from_imm(i)
#define Mr vaddr_read
#define Mw vaddr_write

enum {
    TYPE_R,
    TYPE_I,
    TYPE_S,
    TYPE_B,
    TYPE_U,
    TYPE_J,
    TYPE_N  // none
};

#define src1R()         \
    do {                \
        *src1 = R(rs1); \
    } while (0)
#define src2R()         \
    do {                \
        *src2 = R(rs2); \
    } while (0)
#define immI()                            \
    do {                                  \
        *imm = SEXT(BITS(i, 31, 20), 12); \
    } while (0)
#define immB()                                                                                                            \
    do {                                                                                                                  \
        *imm = (SEXT(BITS(i, 31, 31), 1) << 12) | (BITS(i, 30, 25) << 5) | (BITS(i, 11, 8) << 1) | (BITS(i, 7, 7) << 11); \
    } while (0)
#define immU()                                    \
    do {                                          \
        *imm = (SEXT(BITS(i, 31, 12), 20) << 12); \
    } while (0)
#define immJ()                                                                                                                \
    do {                                                                                                                      \
        *imm = (SEXT(BITS(i, 31, 31), 1) << 20) | (BITS(i, 30, 21) << 1) | (BITS(i, 20, 20) << 11) | (BITS(i, 19, 12) << 12); \
    } while (0)
#define immS()                                                   \
    do {                                                         \
        *imm = (SEXT(BITS(i, 31, 25), 7) << 5) | BITS(i, 11, 7); \
    } while (0)

static void decode_operand(Decode* s, int* rd, word_t* src1, word_t* src2, word_t* imm,word_t* src1imm, int type) {
    uint32_t i = s->isa.inst.val;
    int rs1 = BITS(i, 19, 15);
    int rs2 = BITS(i, 24, 20);
    *rd = BITS(i, 11, 7);
    *src1imm = rs1;
    switch (type) {
        case TYPE_R:
            src1R();
            src2R();
            break;
        case TYPE_I:
            src1R();
            immI();
            break;
        case TYPE_S:
            src1R();
            src2R();
            immS();
            break;
        case TYPE_B:
            src1R();
            src2R();
            immB();
            break;
        case TYPE_U:
            immU();
            break;
        case TYPE_J:
            immJ();
            break;
        case TYPE_N:
            break;
        default:
            panic("Unknown instruction type");
            break;
    }
}

static int decode_exec(Decode* s) {
    int rd = 0;
    word_t src1 = 0, src2 = 0, imm = 0;
    word_t src1imm = 0;
    s->intr = 0;
    s->access_addr = 0;
    s->dnpc = s->snpc;

    // CLINT中断
    if (isa_query_intr() == INTR_CLINT){
        s->intr = 0x80000007;
        cpu.sleep = false;
        cpu.mip |= 1 << 7;  // 中断发生
    }else{
        cpu.mip &= ~(1 << 7);
    }
    if (cpu.sleep){
        s->dnpc = s->pc;
        return 0;
    }

    // 执行中断
    if (( cpu.mip & (1<<7) ) && ( cpu.mie & (1<<7) ) && ( cpu.mstatus & 0x8)){
        s->dnpc = isa_raise_intr(0x80000007, s->pc);
        return 0;
    }

#define INSTPAT_INST(s) ((s)->isa.inst.val)
#define INSTPAT_MATCH(s, name, type, ... /* execute body */)             \
    {                                                                    \
        decode_operand(s, &rd, &src1, &src2, &imm, &src1imm, concat(TYPE_, type)); \
        __VA_ARGS__;                                                     \
    }

    // 可以添加名称参数，来制造多个分段解码
    INSTPAT_START();
    // -----------------------   I   ------------------------------
    INSTPAT("0000000 ????? ????? 000 ????? 01100 11", add, R, R(rd) = src1 + src2);
    INSTPAT("0100000 ????? ????? 000 ????? 01100 11", sub, R, R(rd) = src1 - src2);
    INSTPAT("0000000 ????? ????? 100 ????? 01100 11", xor, R, R(rd) = src1 ^ src2);
    INSTPAT("0000000 ????? ????? 110 ????? 01100 11", or, R, R(rd) = src1 | src2);
    INSTPAT("0000000 ????? ????? 111 ????? 01100 11", and, R, R(rd) = src1 & src2);
    INSTPAT("0000000 ????? ????? 001 ????? 01100 11", sll, R, R(rd) = src1 << BITS(src2, 4, 0));
    INSTPAT("0000000 ????? ????? 010 ????? 01100 11", slt, R, R(rd) = (sword_t)src1 < (sword_t)src2 ? 1 : 0);
    INSTPAT("0000000 ????? ????? 011 ????? 01100 11", sltu, R, R(rd) = src1 < src2 ? 1 : 0);
    INSTPAT("0000000 ????? ????? 101 ????? 01100 11", srl, R, R(rd) = src1 >> BITS(src2, 4, 0));
    INSTPAT("0100000 ????? ????? 101 ????? 01100 11", sra, R, R(rd) = (sword_t)src1 >> BITS(src2, 4, 0));

    INSTPAT("??????? ????? ????? 000 ????? 00100 11", addi, I, R(rd) = src1 + (sword_t)imm);
    INSTPAT("??????? ????? ????? 100 ????? 00100 11", xori, I, R(rd) = src1 ^ imm);
    INSTPAT("??????? ????? ????? 110 ????? 00100 11", ori, I, R(rd) = src1 | imm);
    INSTPAT("??????? ????? ????? 111 ????? 00100 11", andi, I, R(rd) = src1 & imm);
    INSTPAT("0000000 ????? ????? 001 ????? 00100 11", slli, I, R(rd) = src1 << (imm & 0x1f));
    INSTPAT("0000000 ????? ????? 101 ????? 00100 11", srli, I, R(rd) = src1 >> (imm & 0x1f));
    INSTPAT("0100000 ????? ????? 101 ????? 00100 11", srai, I, R(rd) = (sword_t)src1 >> (imm & 0x1f));
    INSTPAT("??????? ????? ????? 010 ????? 00100 11", slti, I, R(rd) = (sword_t)src1 < (sword_t)imm ? 1 : 0);
    INSTPAT("??????? ????? ????? 011 ????? 00100 11", sltiu, I, R(rd) = src1 < imm ? 1 : 0);

    INSTPAT("??????? ????? ????? 000 ????? 00000 11", lb, I, R(rd) = (int8_t)Mr(src1 + imm, 1);s->access_addr = src1 + imm);
    INSTPAT("??????? ????? ????? 001 ????? 00000 11", lh, I, R(rd) = (int16_t)Mr(src1 + imm, 2);s->access_addr = src1 + imm);
    INSTPAT("??????? ????? ????? 010 ????? 00000 11", lw, I, R(rd) = Mr(src1 + imm, 4);s->access_addr = src1 + imm);
    INSTPAT("??????? ????? ????? 100 ????? 00000 11", lbu, I, R(rd) = Mr(src1 + imm, 1);s->access_addr = src1 + imm);
    INSTPAT("??????? ????? ????? 101 ????? 00000 11", lhu, I, R(rd) = Mr(src1 + imm, 2);s->access_addr = src1 + imm);

    INSTPAT("??????? ????? ????? 000 ????? 01000 11", sb, S, Mw(src1 + imm, 1, src2));
    INSTPAT("??????? ????? ????? 001 ????? 01000 11", sh, S, Mw(src1 + imm, 2, src2));
    INSTPAT("??????? ????? ????? 010 ????? 01000 11", sw, S, Mw(src1 + imm, 4, src2));

    INSTPAT("??????? ????? ????? 000 ????? 11000 11", beq, B, if ((sword_t)src1 == (sword_t)src2) s->dnpc = s->pc + imm);
    INSTPAT("??????? ????? ????? 001 ????? 11000 11", bne, B, if ((sword_t)src1 != (sword_t)src2) s->dnpc = s->pc + imm);
    INSTPAT("??????? ????? ????? 100 ????? 11000 11", blt, B, if ((sword_t)src1 < (sword_t)src2) s->dnpc = s->pc + imm);
    INSTPAT("??????? ????? ????? 101 ????? 11000 11", bge, B, if ((sword_t)src1 >= (sword_t)src2) s->dnpc = s->pc + imm);
    INSTPAT("??????? ????? ????? 110 ????? 11000 11", bltu, B, if (src1 < src2) s->dnpc = s->pc + imm);
    INSTPAT("??????? ????? ????? 111 ????? 11000 11", bgeu, B, if (src1 >= src2) s->dnpc = s->pc + imm);

    INSTPAT("??????? ????? ????? ??? ????? 11011 11", jal, J, R(rd) = s->pc + 4; s->dnpc = s->pc + imm);
    INSTPAT("??????? ????? ????? 000 ????? 11001 11", jalr, I, R(rd) = s->pc + 4; s->dnpc = (src1 + imm) & ~1);

    INSTPAT("??????? ????? ????? ??? ????? 01101 11", lui, U, R(rd) = imm);
    INSTPAT("??????? ????? ????? ??? ????? 00101 11", auipc, U, R(rd) = s->pc + imm);

    INSTPAT("0000000 00000 00000 000 00000 11100 11", ecall, N, {
        if(cpu.privilege & 3) s->dnpc = isa_raise_intr(11, s->pc);
        else s->dnpc = isa_raise_intr(8, s->pc);
    });
    INSTPAT("0011000 00010 00000 000 00000 11100 11", mret, N, {
        uint32_t startmstatus = cpu.mstatus;
        uint32_t privilege = cpu.privilege;
        cpu.mstatus = (( startmstatus & 0x80) >> 4) | ((privilege&3) << 11) | 0x80;
        cpu.privilege = (startmstatus >> 11) & 3;
        s->dnpc = cpu.mepc;
    });
    INSTPAT("0001000 00101 00000 000 00000 11100 11", wfi, N, {
        cpu.sleep = true; 
        cpu.mstatus |= 8;
    }); 
    INSTPAT("0000000 00001 00000 000 00000 11100 11", ebreak, N, NEMUTRAP(s->pc, R(10)));  // R(10) is $a0
    // ----------------------- Zicsr ------------------------------
    INSTPAT("??????? ????? ????? 001 ????? 11100 11", csrrw, I, R(rd) = CSR(imm); CSR(imm) = src1);
    INSTPAT("??????? ????? ????? 010 ????? 11100 11", csrrs, I, R(rd) = CSR(imm); CSR(imm) |= src1);
    INSTPAT("??????? ????? ????? 011 ????? 11100 11", csrrc, I, R(rd) = CSR(imm); CSR(imm) &= ~src1);
    INSTPAT("??????? ????? ????? 101 ????? 11100 11", csrrwi, I, R(rd) = CSR(imm); CSR(imm) = src1imm); 
    INSTPAT("??????? ????? ????? 110 ????? 11100 11", csrrsi, I, R(rd) = CSR(imm); CSR(imm) |= src1imm);
    INSTPAT("??????? ????? ????? 111 ????? 11100 11", csrrci, I, R(rd) = CSR(imm); CSR(imm) &= ~src1imm);
    // -----------------------   M   ------------------------------
    INSTPAT("0000001 ????? ????? 000 ????? 01100 11", mul, R, R(rd) = ((int64_t)(sword_t)src1 * (int64_t)(sword_t)src2));
    INSTPAT("0000001 ????? ????? 001 ????? 01100 11", mulh, R, R(rd) = ((int64_t)(sword_t)src1 * (int64_t)(sword_t)src2) >> 32);
    INSTPAT("0000001 ????? ????? 010 ????? 01100 11", mulsu, R, R(rd) = ((int64_t)(sword_t)src1 * (uint64_t)src2) >> 32);
    INSTPAT("0000001 ????? ????? 011 ????? 01100 11", mulu, R, R(rd) = ((uint64_t)src1 * (uint64_t)src2) >> 32);
    INSTPAT("0000001 ????? ????? 100 ????? 01100 11", div, R, {
        if(src2 == 0) R(rd) = -1;
        else R(rd) = ((int32_t)src1 == INT32_MIN && (int32_t)src2 == -1) ? src1 : ((int32_t)src1 / (int32_t)src2);
    });
    INSTPAT("0000001 ????? ????? 101 ????? 01100 11", divu, R, {
        R(rd) = (src2 == 0) ? 0xffffffff : src1 / src2;
    });
    INSTPAT("0000001 ????? ????? 110 ????? 01100 11", rem, R, {
        if(src2 == 0) R(rd) = src1;
        else R(rd) = ((int32_t)src1 == INT32_MIN && (int32_t)src2 == -1) ? 0 : ((uint32_t)((int32_t)src1 % (int32_t)src2));
    });
    INSTPAT("0000001 ????? ????? 111 ????? 01100 11", remu, R, {
        R(rd) = (src2 == 0) ? src1 : src1 % src2;
    });
    // -----------------------   A   ------------------------------
    INSTPAT("00010?? ????? ????? 010 ????? 01011 11", lr.w, R, R(rd) = Mr(src1, 4); cpu.amo_addr = src1); 
    INSTPAT("00011?? ????? ????? 010 ????? 01011 11", sc.w, R, {
        R(rd) = src1 != cpu.amo_addr;
        if (R(rd) == 0) Mw(cpu.amo_addr, 4, src2);
        cpu.amo_addr = 0;
    }); 
    INSTPAT("00001?? ????? ????? 010 ????? 01011 11", amoswap.w, R, R(rd) = Mr(src1, 4); Mw(src1, 4, src2));
    INSTPAT("00000?? ????? ????? 010 ????? 01011 11", amoadd.w, R, R(rd) = Mr(src1, 4); Mw(src1, 4, src2 + R(rd))); 
    INSTPAT("01100?? ????? ????? 010 ????? 01011 11", amoand.w, R, R(rd) = Mr(src1, 4); Mw(src1, 4, src2 & R(rd))); 
    INSTPAT("01000?? ????? ????? 010 ????? 01011 11", amoor.w, R, R(rd) = Mr(src1, 4); Mw(src1, 4, src2 | R(rd))); 
    INSTPAT("00100?? ????? ????? 010 ????? 01011 11", amoxor.w, R, R(rd) = Mr(src1, 4); Mw(src1, 4, src2 ^ R(rd))); 
    INSTPAT("10100?? ????? ????? 010 ????? 01011 11", amomax.w, R, R(rd) = Mr(src1, 4); Mw(src1, 4, ((sword_t)src2 > (sword_t)R(rd))?src2:R(rd))); 
    INSTPAT("10000?? ????? ????? 010 ????? 01011 11", amomin.w, R, R(rd) = Mr(src1, 4); Mw(src1, 4, ((sword_t)src2 < (sword_t)R(rd))?src2:R(rd))); 
    INSTPAT("11000?? ????? ????? 010 ????? 01011 11", amominu.w, R, R(rd) = Mr(src1, 4); Mw(src1, 4, (src2 < R(rd))?src2:R(rd))); 
    INSTPAT("11100?? ????? ????? 010 ????? 01011 11", amomaxu.w, R, R(rd) = Mr(src1, 4); Mw(src1, 4, (src2 > R(rd))?src2:R(rd))); 
    // ----------------------- Zifence ------------------------------
    INSTPAT("??????? ????? ????? 000 ????? 00011 11", fence, N, ); 
    INSTPAT("??????? ????? ????? 001 ????? 00011 11", fence.i, N, ); 

    INSTPAT("??????? ????? ????? ??? ????? ????? ??", inv, N, INV(s->pc));
    INSTPAT_END();
    R(0) = 0;  // reset $zero to 0

    return 0;
}

int isa_exec_once(Decode* s) {
    s->isa.inst.val = inst_fetch(&s->snpc, 4);
    IFDEF(CONFIG_ITRACE, itrace_insert(s->pc, s->isa.inst.val));
    return decode_exec(s);
}
