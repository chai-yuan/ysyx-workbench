module top (
    input clk,
    input rst,
    input [15:0] sw,
    input ps2_clk,
    input ps2_data,
    input BTNC,
    input BTNU,
    input BTND,
    input BTNL,
    input BTNR,
    output [15:0] ledr,
    output VGA_CLK,
    output VGA_HSYNC,
    output VGA_VSYNC,
    output VGA_BLANK_N,
    output [7:0] VGA_R,
    output [7:0] VGA_G,
    output [7:0] VGA_B,
    output [7:0] seg0,
    output [7:0] seg1,
    output [7:0] seg2,
    output [7:0] seg3,
    output [7:0] seg4,
    output [7:0] seg5,
    output [7:0] seg6,
    output [7:0] seg7
);

    wire [7:0] keycode,input_keycode;
    wire [7:0] ascii;
    wire ready;
    reg nextdata_n;
    reg [31:0] data;
    reg [7:0] enable,key_cnt;
    reg [7:0] time_cnt;
    
    Ps2Keyboard keyboard (
        .clk(clk), 
        .clrn(~rst), 
        .ps2_clk(ps2_clk), 
        .ps2_data(ps2_data),
        .nextdata_n(nextdata_n),
        .data(input_keycode),
        .ready(ready),
        .overflow()
    );
    assign keycode = input_keycode & {8{ready}};

    Key2ASCII converter (
        .key_code(keycode), 
        .ASCII(ascii)
    );
    
    always @(posedge clk) begin
        if(time_cnt != 8'h00)begin
            time_cnt <= time_cnt - 8'h01;
        end
    end
    
    always @(posedge clk) begin
        if(keycode == 8'hF0)begin
           key_cnt <= key_cnt + 8'h01;
           enable <= 8'b1100_0000; 
           time_cnt <= 8'hff;
        end else if(keycode != 8'h00 && time_cnt == 8'h00)begin
            data <= {key_cnt,8'h00,ascii,keycode};
            enable <= 8'b1100_1111;
        end 
    end
    
    SegmentLED display (
        .clk(clk), 
        .data(data), 
        .enable(enable),
        .seg0(seg0), 
        .seg1(seg1), 
        .seg2(seg2), 
        .seg3(seg3), 
        .seg4(seg4), 
        .seg5(seg5), 
        .seg6(seg6), 
        .seg7(seg7)
    );

endmodule
