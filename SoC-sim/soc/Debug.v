module Debug (
    input wire clock,
    input wire reset,

    input wire          debug_valid,
    input wire          debug_halt,

    input wire          debug_deviceAccess,
    input wire [31:0]   debug_deviceAddr,

    input wire [31:0]   debug_pc,
    input wire          debug_regWen,
    input wire [4:0]    debug_regWaddr,
    input wire [31:0]   debug_regWdata
);
    import "DPI-C" function void debug_sim_halt();

    import "DPI-C" function void debug_update_cpu(int deviceAccess,
                                                int deviceAddr,
                                                int pc,
                                                int regWen,
                                                int regWaddr,
                                                int regWdata);

    wire valid = !reset && debug_valid;

    // 因为pc数值指代的是当前指令的pc值，为了和difftest的next_pc值做比较，
    // 在这里延后1条指令再进行对比
    reg valid_reg;
    reg deviceAccess;
    reg [31:0] deviceAddr;
    reg regWen;
    reg [4:0] regWaddr;
    reg [31:0] regWdata;
    always @(posedge clock) begin
        if(valid) begin
            valid_reg <= valid;
            deviceAccess <= debug_deviceAccess;
            deviceAddr <= debug_deviceAddr;
            regWen <= debug_regWen;
            regWaddr <= debug_regWaddr;
            regWdata <= debug_regWdata;
        end
    end

    always @(posedge clock) begin
        if(valid && valid_reg) begin
            debug_update_cpu(deviceAccess,deviceAddr
                            ,debug_pc, regWen, regWaddr, regWdata);

            if(debug_halt)begin
                debug_sim_halt();
            end
        end
    end

endmodule
