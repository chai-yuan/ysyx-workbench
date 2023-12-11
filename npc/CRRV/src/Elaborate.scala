import core.CPUTop
import circt.stage._

object Elaborate extends App {
  def top = new CPUTop

  val chiselStageOptions = Seq(
    chisel3.stage.ChiselGeneratorAnnotation(() => top),
    CIRCTTargetAnnotation(CIRCTTarget.Verilog)
  )

  val firtoolOptions = Seq(
    FirtoolOption("--split-verilog"),
    FirtoolOption("-o=build/verilog-gen"),

    // FirtoolOption("--lowering-options=noAlwaysComb"),   // 生成更低要求的verilog代码
    // FirtoolOption("--lowering-options=disallowLocalVariables"),
    // FirtoolOption("--lowering-options=disallowPackedArrays"),

    FirtoolOption("--disable-all-randomization")
  )

  val executeOptions = chiselStageOptions ++ firtoolOptions

  (new ChiselStage).execute(args, executeOptions)
}
