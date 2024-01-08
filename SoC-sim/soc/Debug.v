module Debug (
    input wire clock,
    input wire reset,

    input wire          debug_valid,
    input wire          debug_halt,
    input wire [31:0]   debug_pc,
    input wire          debug_regWen,
    input wire [4:0]    debug_regWaddr,
    input wire [31:0]   debug_regWdata
);
    
endmodule
