module top (
	input wire clk, 
	input wire rst, 

	//intruction sram			from ifu
	output wire isram_e, // enable instruction sram read 1-enable, 0-disenable
	output wire [31: 0] isram_addr,  // instruction address - pc
	//							send to idu
	input  wire [31: 0] isram_rdata, // instruction

	//data sram					from exu
	output wire dsram_e,  // enable data sram, write or read, 1-e,0-d
	output wire dsram_we, // enable data sram, write

	//difftest need				 from write back 
	output wire [31: 0] debug_wb_pc, // u can change this wire
	output wire [31: 0] debug_wb_npc, 
	output bubble     // if it is a bubble,"c" will skip difftest once  
);
	// mem
	wire [31: 0] dsram_addr; //read or wire need data-sram address
	wire [31: 0] dsram_wdata; //data sram write data 
	wire [ 3: 0] dsram_sel;   //dataa sram write size selection
						      // 64bits 1111_1111 32bits 0000_1111 
							  // 16bits 0000_0011 8 bits 0000_0001
	wire [31: 0] dsram_rdata; //read data-sram dara
    // halt
    wire debugHalt;
    SimulationHalt simulation_halt(
        debugHalt
    );

    CPUTop cputop(
        .clock(clk),
        .reset(rst),
        .io_instSRAM_en(isram_e),
        .io_instSRAM_we(),
        .io_instSRAM_addr(isram_addr),
        .io_instSRAM_rdata(isram_rdata),
        .io_instSRAM_wdata(),

        .io_debug_debugPC(debug_wb_pc),
        .io_debug_debugHalt(debugHalt)
    );

endmodule

module SimulationMem (
    input [31:0] dsram_addr,
    input [31:0] dsram_wdata,
    input [3:0] dsram_sel,
    output [31:0] dsram_rdata
);
   	//data sram read "DPI-C"  if u have new idea, u will chang here and "C".
	import "DPI-C" function void mem_read(   
		input longint raddr, output longint rdata );
	//data sram write "DPI-C"
	import "DPI-C" function void mem_write (
		input longint waddr, input longint wdata, input byte wmask );

	always @(*) begin
		mem_read(dsram_addr, dsram_rdata); 
		//read 64bits data, u need code your exe moudle to cut 8\16\32\64bits
		mem_write(dsram_addr, dsram_wdata, dsram_sel);
	end   
    
endmodule


module SimulationHalt(
    input wire debugHalt
);
    import "DPI-C" function void ebreak();
    
    always @(*) begin
        if (debugHalt)
            ebreak();
    end
endmodule
