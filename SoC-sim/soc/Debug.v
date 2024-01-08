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
    import "DPI-C" function void debug_sim_halt();

    wire valid = !reset && debug_valid;

    always @(posedge clock) begin
        if(valid && debug_halt) begin
            debug_sim_halt();
        end
    end

endmodule
