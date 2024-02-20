module gpio_top_apb(
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

  output [15:0] gpio_out,
  input  [15:0] gpio_in,
  output [7:0]  gpio_seg_0,
  output [7:0]  gpio_seg_1,
  output [7:0]  gpio_seg_2,
  output [7:0]  gpio_seg_3,
  output [7:0]  gpio_seg_4,
  output [7:0]  gpio_seg_5,
  output [7:0]  gpio_seg_6,
  output [7:0]  gpio_seg_7
);

// 寄存器同gpio输出绑定
reg [15:0] led;
reg [15:0] switch;
reg [7:0] seg [7:0];
assign gpio_out = led;
assign gpio_seg_0 = seg[0];
assign gpio_seg_1 = seg[1];
assign gpio_seg_2 = seg[2];
assign gpio_seg_3 = seg[3];
assign gpio_seg_4 = seg[4];
assign gpio_seg_5 = seg[5];
assign gpio_seg_6 = seg[6];
assign gpio_seg_7 = seg[7];
always @(posedge clock) begin
  if(!reset) begin
    switch <= gpio_in;
  end
end

// 处理总线事务
reg [31:0] read_data;
assign in_prdata = read_data;
assign in_pready = 1'b1;
assign in_pslverr = 1'b0;
always @(posedge clock) begin
  if(!reset) begin
    // 执行写入
    if(in_pwrite && in_psel && in_penable) begin
      case(in_paddr[3:2]) 
        2'b00 : begin // led
          led[7:0] <= in_pstrb[0] ? in_pwdata[7:0] : led[7:0];
          led[15:8] <= in_pstrb[1] ? in_pwdata[15:8] : led[15:8];
        end
        2'b10 : begin // 数码管
          $display("TODO : segment led");
        end
        default : begin
        end
      endcase

    end
    // 执行读取
    if(!in_pwrite && in_psel) begin
      read_data[15:0] <= switch[15:0];
    end
  end
end

endmodule
