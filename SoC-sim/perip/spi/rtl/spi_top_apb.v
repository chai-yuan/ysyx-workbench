// define this macro to enable fast behavior simulation
// for flash by skipping SPI transfers
// `define FAST_FLASH

module spi_top_apb #(
  parameter flash_addr_start = 32'h30000000,
  parameter flash_addr_end   = 32'h3fffffff,
  parameter spi_ss_num       = 8
) (
  input         clock,
  input         reset,
  input  [31:0] in_paddr,
  input         in_psel,
  input         in_penable,
  input  [2:0]  in_pprot,
  input         in_pwrite,
  input  [31:0] in_pwdata,
  input  [3:0]  in_pstrb,
  output        in_pready,
  output [31:0] in_prdata,
  output        in_pslverr,

  output                  spi_sck,
  output [spi_ss_num-1:0] spi_ss,
  output                  spi_mosi,
  input                   spi_miso,
  output                  spi_irq_out
);

`ifdef FAST_FLASH

wire [31:0] data;
parameter invalid_cmd = 8'h0;
flash_cmd flash_cmd_i(
  .clock(clock),
  .valid(in_psel && !in_penable),
  .cmd(in_pwrite ? invalid_cmd : 8'h03),
  .addr({8'b0, in_paddr[23:2], 2'b0}),
  .data(data)
);
assign spi_sck    = 1'b0;
assign spi_ss     = 8'b0;
assign spi_mosi   = 1'b1;
assign spi_irq_out= 1'b0;
assign in_pslverr = 1'b0;
assign in_pready  = in_penable && in_psel && !in_pwrite;
assign in_prdata  = data[31:0];

`else

wire [4:0]  spi_paddr;
wire [31:0] spi_pwdata;
wire [31:0] spi_prdata;
wire [3:0]  spi_pstrb;
wire        spi_pwrite;
wire        spi_psel;
wire        spi_enable;
wire        spi_pready;
wire        spi_irq;

wire        is_flash;
assign is_flash = flash_addr_start <= in_paddr &&
                  flash_addr_end  >=  in_paddr &&
                  in_psel && in_penable;

// flash直接访问控制状态机
`define IDLE  3'h0
`define DIV   3'h1
`define SS    3'h2
`define TX    3'h3
`define CTRL  3'h4
`define WAIT  3'h5
`define RX    3'h6
reg [2:0]   flash_state;
always @(posedge clock or posedge reset or posedge spi_irq_out) begin
  if(reset) begin
    flash_state <= `IDLE;
  end
  else begin
    case (flash_state)
      `IDLE : begin
        if(is_flash)
          flash_state <= `DIV;
      end
      `DIV : begin
        if(apb_fire)
          flash_state <= `SS;
      end
      `SS : begin
        if(apb_fire)
          flash_state <= `TX;
      end
      `TX : begin
        if(apb_fire)
          flash_state <= `CTRL;
      end
      `CTRL : begin
        if(apb_fire)
          flash_state <= `WAIT;
      end
      `WAIT : begin
        if(spi_irq) 
          flash_state <= `RX;
      end
      default : begin
        if(apb_fire)
          flash_state <= `IDLE;
      end

    endcase
  end
end

// apb通讯控制状态机
wire        apb_start;
wire        apb_fire;
reg [2:0]   apb_state;

assign apb_start = flash_state != `IDLE && flash_state != `WAIT;
assign apb_fire = apb_state == `WAIT && spi_pready;

always @(posedge clock or posedge reset) begin
  if(reset) begin
    apb_state <= `IDLE;
  end
  else begin
    case (apb_state)
      `IDLE : begin
        if(apb_start)
          apb_state <= `TX;
      end  
      
      `TX : begin
        apb_state <= `WAIT;
      end

      default : begin
        if (spi_pready) 
          apb_state <= `IDLE;
      end

    endcase
  end
end

assign spi_paddr = flash_state == `IDLE ? in_paddr :
                   flash_state == `DIV  ? 5'h14    :
                   flash_state == `SS   ? 5'h18    :
                   flash_state == `TX   ? 5'h04    :
                   flash_state == `CTRL ? 5'h10    :
                   flash_state == `RX   ? 5'h00    :
                   5'h00;
assign spi_pwdata =flash_state == `IDLE ? in_pwdata:
                   flash_state == `DIV  ? 32'h1    :
                   flash_state == `SS   ? 32'h1    :
                   flash_state == `TX   ? {8'h03, in_paddr[23:0]}:
                   flash_state == `CTRL ? 32'b11010101000000     :
                   32'h00;
assign in_prdata = flash_state == `IDLE ? spi_prdata:
                   {spi_prdata[7:0],spi_prdata[15:8],spi_prdata[23:16],spi_prdata[31:24]};
assign spi_psel  = flash_state == `IDLE ? in_psel :
                   apb_state != `IDLE;
assign spi_pwrite =flash_state == `IDLE ? in_pwrite :
                   flash_state == `RX   ? 1'b0      :
                   1'b1;
assign spi_pstrb = 4'hf;
assign spi_penable = flash_state == `IDLE ? in_penable :
                   apb_state == `WAIT;
assign in_pready = flash_state == `IDLE ? spi_pready :
                   flash_state == `RX && apb_fire;
assign spi_irq_out = flash_state == `IDLE ? spi_irq : 1'b0;      

spi_top u0_spi_top (
  .wb_clk_i(clock),
  .wb_rst_i(reset),
  .wb_adr_i(spi_paddr),
  .wb_dat_i(spi_pwdata),
  .wb_dat_o(spi_prdata),
  .wb_sel_i(spi_pstrb),
  .wb_we_i (spi_pwrite),
  .wb_stb_i(spi_psel),
  .wb_cyc_i(spi_penable),
  .wb_ack_o(spi_pready),
  .wb_err_o(in_pslverr),
  .wb_int_o(spi_irq),

  .ss_pad_o(spi_ss),
  .sclk_pad_o(spi_sck),
  .mosi_pad_o(spi_mosi),
  .miso_pad_i(spi_miso)
);

`endif // FAST_FLASH

endmodule
