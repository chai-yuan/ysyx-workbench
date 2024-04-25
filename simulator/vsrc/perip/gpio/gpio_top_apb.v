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
reg [31:0] seg_data;
assign gpio_out = led;
DigitDriver seg_0(seg_data[3:0], gpio_seg_0);
DigitDriver seg_1(seg_data[7:4], gpio_seg_1);
DigitDriver seg_2(seg_data[11:8], gpio_seg_2);
DigitDriver seg_3(seg_data[15:12], gpio_seg_3);
DigitDriver seg_4(seg_data[19:16], gpio_seg_4);
DigitDriver seg_5(seg_data[23:20], gpio_seg_5);
DigitDriver seg_6(seg_data[27:24], gpio_seg_6);
DigitDriver seg_7(seg_data[31:28], gpio_seg_7);
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
          seg_data[7:0] <= in_pstrb[0] ? in_pwdata[7:0] : seg_data[7:0];
          seg_data[15:8] <= in_pstrb[1] ? in_pwdata[15:8] : seg_data[15:8];
          seg_data[23:16] <= in_pstrb[2] ? in_pwdata[23:16] : seg_data[23:16];
          seg_data[31:24] <= in_pstrb[3] ? in_pwdata[31:24] : seg_data[31:24];
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

// 数码管驱动模块
module DigitDriver (
  input wire [3:0] seg_data,
  output reg [7:0] gpio_seg
);

  always @(*) begin
    case (seg_data)
      4'b0000: gpio_seg = 8'b00000011; // Digit 0
      4'b0001: gpio_seg = 8'b10011111; // Digit 1
      4'b0010: gpio_seg = 8'b00100101; // Digit 2
      4'b0011: gpio_seg = 8'b00001101; // Digit 3
      4'b0100: gpio_seg = 8'b10011001; // Digit 4
      4'b0101: gpio_seg = 8'b01001001; // Digit 5
      4'b0110: gpio_seg = 8'b01000001; // Digit 6
      4'b0111: gpio_seg = 8'b00011111; // Digit 7
      4'b1000: gpio_seg = 8'b00000001; // Digit 8
      4'b1001: gpio_seg = 8'b00001001; // Digit 9
      // 4'b1010: gpio_seg = 8'b11111111; // Digit A
      // 4'b1011: gpio_seg = 8'b11111111; // Digit B
      // 4'b1100: gpio_seg = 8'b11111111; // Digit C
      // 4'b1101: gpio_seg = 8'b11111111; // Digit D
      // 4'b1110: gpio_seg = 8'b11111111; // Digit E
      // 4'b1111: gpio_seg = 8'b11111111; // Digit F
      default: gpio_seg = 8'b11111111; // Default case
    endcase
  end

endmodule
