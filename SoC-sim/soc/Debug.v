module Debug (
    input wire clock,
    input wire reset,
    input wire          debug_debugInfo_valid,
    input wire          debug_debugInfo_halt,
    input wire          debug_debugInfo_deviceAccess,
    input wire [31:0]   debug_debugInfo_deviceAddr,
    input wire [31:0]   debug_debugInfo_pc,

    input wire [31:0]   debug_regs_regs_0,
    input wire [31:0]   debug_regs_regs_1,
    input wire [31:0]   debug_regs_regs_2,
    input wire [31:0]   debug_regs_regs_3,
    input wire [31:0]   debug_regs_regs_4,
    input wire [31:0]   debug_regs_regs_5,
    input wire [31:0]   debug_regs_regs_6,
    input wire [31:0]   debug_regs_regs_7,
    input wire [31:0]   debug_regs_regs_8,
    input wire [31:0]   debug_regs_regs_9,
    input wire [31:0]   debug_regs_regs_10,
    input wire [31:0]   debug_regs_regs_11,
    input wire [31:0]   debug_regs_regs_12,
    input wire [31:0]   debug_regs_regs_13,
    input wire [31:0]   debug_regs_regs_14,
    input wire [31:0]   debug_regs_regs_15,
    input wire [31:0]   debug_regs_regs_16,
    input wire [31:0]   debug_regs_regs_17,
    input wire [31:0]   debug_regs_regs_18,
    input wire [31:0]   debug_regs_regs_19,
    input wire [31:0]   debug_regs_regs_20,
    input wire [31:0]   debug_regs_regs_21,
    input wire [31:0]   debug_regs_regs_22,
    input wire [31:0]   debug_regs_regs_23,
    input wire [31:0]   debug_regs_regs_24,
    input wire [31:0]   debug_regs_regs_25,
    input wire [31:0]   debug_regs_regs_26,
    input wire [31:0]   debug_regs_regs_27,
    input wire [31:0]   debug_regs_regs_28,
    input wire [31:0]   debug_regs_regs_29,
    input wire [31:0]   debug_regs_regs_30,
    input wire [31:0]   debug_regs_regs_31,

    input wire [31:0]   debug_csr_mstatus,
    input wire [31:0]   debug_csr_mcause,
    input wire [31:0]   debug_csr_mtvec,
    input wire [31:0]   debug_csr_mepc,
    input wire [31:0]   debug_csr_mscratch,
    input wire [31:0]   debug_csr_mie,
    input wire [31:0]   debug_csr_mip,
    input wire [31:0]   debug_csr_mtval,

    input wire          debug_intr
);

import "DPI-C" function void debug_sim_halt();
import "DPI-C" function void debug_sim_intr();
import "DPI-C" function void debug_update_csr(int mstatus, int mcause, int mtvec, int mepc,
                                 int mscratch, int mie, int mip, int mtval);
import "DPI-C" function void debug_update_reg(
    int reg0, int reg1, int reg2, int reg3, int reg4, int reg5, int reg6, int reg7, int reg8, int reg9,
    int reg10, int reg11, int reg12, int reg13, int reg14, int reg15, int reg16, int reg17, int reg18, int reg19,
    int reg20, int reg21, int reg22, int reg23, int reg24, int reg25, int reg26, int reg27, int reg28, int reg29,
    int reg30, int reg31
);
import "DPI-C" function void debug_update_cpu(int deviceAccess,
                                 int deviceAddr,
                                 int pc,
                                 );

wire valid = !reset && debug_debugInfo_valid;
always @(posedge clock) begin
debug_update_csr(
    debug_csr_mstatus,debug_csr_mcause,debug_csr_mtvec,debug_csr_mepc,
    debug_csr_mscratch,debug_csr_mie,debug_csr_mip,debug_csr_mtval
);

debug_update_reg(
    debug_regs_regs_0, debug_regs_regs_1, debug_regs_regs_2, debug_regs_regs_3, debug_regs_regs_4,
    debug_regs_regs_5, debug_regs_regs_6, debug_regs_regs_7, debug_regs_regs_8, debug_regs_regs_9,
    debug_regs_regs_10, debug_regs_regs_11, debug_regs_regs_12, debug_regs_regs_13, debug_regs_regs_14,
    debug_regs_regs_15, debug_regs_regs_16, debug_regs_regs_17, debug_regs_regs_18, debug_regs_regs_19,
    debug_regs_regs_20, debug_regs_regs_21, debug_regs_regs_22, debug_regs_regs_23, debug_regs_regs_24,
    debug_regs_regs_25, debug_regs_regs_26, debug_regs_regs_27, debug_regs_regs_28, debug_regs_regs_29,
    debug_regs_regs_30, debug_regs_regs_31
);

if(debug_intr) begin
    debug_sim_intr();
end


if(valid) begin
    debug_update_cpu(debug_debugInfo_deviceAccess,debug_debugInfo_deviceAddr,debug_debugInfo_pc);
    if(debug_debugInfo_halt)begin
        debug_sim_halt();
    end
end

end
endmodule


// module Debug (
//     input wire clock,
//     input wire reset,
//
//     input wire          debug_valid,
//     input wire          debug_halt,
//
//     input wire          debug_deviceAccess,
//     input wire [31:0]   debug_deviceAddr,
//
//     input wire [31:0]   debug_pc,
//     input wire          debug_regWen,
//     input wire [4:0]    debug_regWaddr,
//     input wire [31:0]   debug_regWdata
// );
//     import "DPI-C" function void debug_sim_halt();
//
//     import "DPI-C" function void debug_update_cpu(int deviceAccess,
//                                                 int deviceAddr,
//                                                 int pc,
//                                                 int regWen,
//                                                 int regWaddr,
//                                                 int regWdata);
//
//     wire valid = !reset && debug_valid;
//
//     // 因为pc数值指代的是当前指令的pc值，为了和difftest的next_pc值做比较，
//     // 在这里延后1条指令再进行对比
//     reg valid_reg;
//     reg deviceAccess;
//     reg [31:0] deviceAddr;
//     reg regWen;
//     reg [4:0] regWaddr;
//     reg [31:0] regWdata;
//     always @(posedge clock) begin
//         if(valid) begin
//             valid_reg <= valid;
//             deviceAccess <= debug_deviceAccess;
//             deviceAddr <= debug_deviceAddr;
//             regWen <= debug_regWen;
//             regWaddr <= debug_regWaddr;
//             regWdata <= debug_regWdata;
//         end
//     end
//
//     always @(posedge clock) begin
//         if(valid && valid_reg) begin
//             debug_update_cpu(deviceAccess,deviceAddr
//                             ,debug_pc, regWen, regWaddr, regWdata);
//
//             if(debug_halt)begin
//                 debug_sim_halt();
//             end
//         end
//     end
//
// endmodule
