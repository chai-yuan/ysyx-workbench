module bitrev (
  input  sck,     // 时钟信号
  input  ss,      // 片选信号，低电平有效
  input  mosi,    // 主设备输出从设备输入信号
  output miso // 主设备输入从设备输出信号
);

// 定义状态机的状态
typedef enum {IDLE, RECEIVE, SEND} state_t;
state_t state = IDLE;

// 用于存储接收到的数据和发送数据的寄存器
reg [7:0] received_data;
reg [7:0] bit_count; // 用于计数

assign miso = (state == SEND) & received_data[bit_count];

// 用于描述状态机的行为
always @(posedge sck or negedge ss) begin
    case (state)
      IDLE: begin
        if(!ss) begin
          state <= RECEIVE;
          bit_count <= 7;
        end
      end

      RECEIVE: begin
          // 接收数据
          received_data[bit_count] <= mosi;
          bit_count <= bit_count == 0 ? 0 : bit_count - 1;
          state <= bit_count == 0 ? SEND : RECEIVE;
      end

      SEND: begin
          bit_count <= bit_count + 1;
          if (bit_count == 7) begin
            // 如果已经发送了8位数据，转回空闲状态
            state <= IDLE;
          end
      end
      default: state <= IDLE;
    endcase
end

endmodule
