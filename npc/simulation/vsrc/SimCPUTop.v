module SimCPUTop(	// @[<stdin>:711:10]
  input         clock,	// @[<stdin>:712:11]
                reset,	// @[<stdin>:713:11]

  output [31:0] io_debug_regs_0,	// @[src/src/core/CPUTop.scala:22:14]
                io_debug_regs_1,	// @[src/src/core/CPUTop.scala:22:14]
                io_debug_regs_2,	// @[src/src/core/CPUTop.scala:22:14]
                io_debug_regs_3,	// @[src/src/core/CPUTop.scala:22:14]
                io_debug_regs_4,	// @[src/src/core/CPUTop.scala:22:14]
                io_debug_regs_5,	// @[src/src/core/CPUTop.scala:22:14]
                io_debug_regs_6,	// @[src/src/core/CPUTop.scala:22:14]
                io_debug_regs_7,	// @[src/src/core/CPUTop.scala:22:14]
                io_debug_regs_8,	// @[src/src/core/CPUTop.scala:22:14]
                io_debug_regs_9,	// @[src/src/core/CPUTop.scala:22:14]
                io_debug_regs_10,	// @[src/src/core/CPUTop.scala:22:14]
                io_debug_regs_11,	// @[src/src/core/CPUTop.scala:22:14]
                io_debug_regs_12,	// @[src/src/core/CPUTop.scala:22:14]
                io_debug_regs_13,	// @[src/src/core/CPUTop.scala:22:14]
                io_debug_regs_14,	// @[src/src/core/CPUTop.scala:22:14]
                io_debug_regs_15,	// @[src/src/core/CPUTop.scala:22:14]
                io_debug_regs_16,	// @[src/src/core/CPUTop.scala:22:14]
                io_debug_regs_17,	// @[src/src/core/CPUTop.scala:22:14]
                io_debug_regs_18,	// @[src/src/core/CPUTop.scala:22:14]
                io_debug_regs_19,	// @[src/src/core/CPUTop.scala:22:14]
                io_debug_regs_20,	// @[src/src/core/CPUTop.scala:22:14]
                io_debug_regs_21,	// @[src/src/core/CPUTop.scala:22:14]
                io_debug_regs_22,	// @[src/src/core/CPUTop.scala:22:14]
                io_debug_regs_23,	// @[src/src/core/CPUTop.scala:22:14]
                io_debug_regs_24,	// @[src/src/core/CPUTop.scala:22:14]
                io_debug_regs_25,	// @[src/src/core/CPUTop.scala:22:14]
                io_debug_regs_26,	// @[src/src/core/CPUTop.scala:22:14]
                io_debug_regs_27,	// @[src/src/core/CPUTop.scala:22:14]
                io_debug_regs_28,	// @[src/src/core/CPUTop.scala:22:14]
                io_debug_regs_29,	// @[src/src/core/CPUTop.scala:22:14]
                io_debug_regs_30,	// @[src/src/core/CPUTop.scala:22:14]
                io_debug_regs_31,	// @[src/src/core/CPUTop.scala:22:14]

  output        io_debug_wbDebug_valid,	// @[src/src/core/CPUTop.scala:22:14]
  output [31:0] io_debug_wbDebug_pc,	// @[src/src/core/CPUTop.scala:22:14]
  output [31:0] io_debug_wbDebug_inst,	// @[src/src/core/CPUTop.scala:22:14]
  output        io_debug_wbDebug_halt	// @[src/src/core/CPUTop.scala:22:14]
);

  wire [31:0] io_inst_readData;
  wire [31:0] io_data_readData;
  wire [31:0] io_inst_addr;
  wire        io_inst_writeEn;
  wire [31:0] io_inst_writeData;
  wire        io_inst_readEn;
  wire [3:0]  io_inst_mark;
  wire [31:0] io_data_addr;
  wire        io_data_writeEn;
  wire [31:0] io_data_writeData;
  wire        io_data_readEn;
  wire [3:0]  io_data_mark;

  SimInstMem instMem (
    .clk(clock),
    .reset(reset),

    .writeEn(io_inst_writeEn),
    .readEn(io_inst_readEn),
    .mark(io_inst_mark),
    .addr(io_inst_addr),
    .writeData(io_inst_writeData),
    .readData(io_inst_readData)
  );

  SimDataMem dataMem (
    .clk(clock),
    .reset(reset),

    .writeEn(io_data_writeEn),
    .readEn(io_data_readEn),
    .mark(io_data_mark),
    .addr(io_data_addr),
    .writeData(io_data_writeData),
    .readData(io_data_readData)
  );

  CPUTop cpu (
    .clock(clock),
    .reset(reset),
    .io_inst_readData(io_inst_readData),
    .io_data_readData(io_data_readData),
    .io_inst_addr(io_inst_addr),
    .io_inst_writeEn(io_inst_writeEn),
    .io_inst_writeData(io_inst_writeData),
    .io_inst_readEn(io_inst_readEn),
    .io_inst_mark(io_inst_mark),
    .io_data_addr(io_data_addr),
    .io_data_writeEn(io_data_writeEn),
    .io_data_writeData(io_data_writeData),
    .io_data_readEn(io_data_readEn),
    .io_data_mark(io_data_mark),

    .io_debug_regs_0(io_debug_regs_0),
    .io_debug_regs_1(io_debug_regs_1),
    .io_debug_regs_2(io_debug_regs_2),
    .io_debug_regs_3(io_debug_regs_3),
    .io_debug_regs_4(io_debug_regs_4),
    .io_debug_regs_5(io_debug_regs_5),
    .io_debug_regs_6(io_debug_regs_6),
    .io_debug_regs_7(io_debug_regs_7),
    .io_debug_regs_8(io_debug_regs_8),
    .io_debug_regs_9(io_debug_regs_9),
    .io_debug_regs_10(io_debug_regs_10),
    .io_debug_regs_11(io_debug_regs_11),
    .io_debug_regs_12(io_debug_regs_12),
    .io_debug_regs_13(io_debug_regs_13),
    .io_debug_regs_14(io_debug_regs_14),
    .io_debug_regs_15(io_debug_regs_15),
    .io_debug_regs_16(io_debug_regs_16),
    .io_debug_regs_17(io_debug_regs_17),
    .io_debug_regs_18(io_debug_regs_18),
    .io_debug_regs_19(io_debug_regs_19),
    .io_debug_regs_20(io_debug_regs_20),
    .io_debug_regs_21(io_debug_regs_21),
    .io_debug_regs_22(io_debug_regs_22),
    .io_debug_regs_23(io_debug_regs_23),
    .io_debug_regs_24(io_debug_regs_24),
    .io_debug_regs_25(io_debug_regs_25),
    .io_debug_regs_26(io_debug_regs_26),
    .io_debug_regs_27(io_debug_regs_27),
    .io_debug_regs_28(io_debug_regs_28),
    .io_debug_regs_29(io_debug_regs_29),
    .io_debug_regs_30(io_debug_regs_30),
    .io_debug_regs_31(io_debug_regs_31),

    .io_debug_wbDebug_valid(io_debug_wbDebug_valid),
    .io_debug_wbDebug_pc(io_debug_wbDebug_pc),
    .io_debug_wbDebug_inst(io_debug_wbDebug_inst),
    .io_debug_wbDebug_halt(io_debug_wbDebug_halt)
  );

endmodule

module SimInstMem (
    input clk,
    input reset,

    input writeEn,
    input readEn,
    input [3:0] mark,
    input [31:0] addr,
    input [31:0] writeData,
    output reg [31:0] readData
);
    import "DPI-C" function void verilog_pmem_read(input int raddr, output int rdata);
    import "DPI-C" function void verilog_pmem_write(input int waddr, input int wdata, input byte wmask);

    always @(posedge clk) begin
        if (readEn && !reset) begin
            verilog_pmem_read(addr, readData);
        end
    end
endmodule

module SimDataMem (
    input clk,
    input reset,
    
    input writeEn,
    input readEn,
    input [3:0] mark,
    input [31:0] addr,
    input [31:0] writeData,
    output reg [31:0] readData
);
    import "DPI-C" function void verilog_pmem_read(input int raddr, output int rdata);
    import "DPI-C" function void verilog_pmem_write(input int waddr, input int wdata, input byte wmask);

    always @(posedge clk) begin
        if (writeEn && !reset) begin
            verilog_pmem_write(addr, writeData, mark);
        end
        if (readEn && !reset) begin
            verilog_pmem_read(addr, readData);
        end
    end
    
endmodule

