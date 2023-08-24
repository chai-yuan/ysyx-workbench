package tools

import chisel3._
import chisel3.util._

class SimulationExit extends BlackBox with HasBlackBoxInline {
  val io = IO(new Bundle {
    val exit = Input(Bool())
  })

  setInline(
    "simulation_exit.v",
    """
      |module SimulationExit(
      |    input wire exit
      |);
      |    import "DPI-C" function void simulation_exit();
      |
      |    always @(posedge exit) begin
      |         simulation_exit();
      |    end
      |endmodule
            """.stripMargin
  )
}
