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

    import "DPI-C" function void debug_update_cpu(int pc,
                                            int regWen,
                                            int regWaddr,
                                            int regWdata);

    wire valid = !reset && debug_valid;

    always @(posedge clock) begin
        if(valid) begin
            debug_update_cpu(debug_pc, debug_regWen, debug_regWaddr, debug_regWdata);

            if(debug_halt)begin
                debug_sim_halt();
            end
        end
    end

endmodule
