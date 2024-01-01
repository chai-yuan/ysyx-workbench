import core.CRRVTop
import circt.stage._

object Elaborate extends App {
  def top = new CRRVTop

  val chiselStageOptions = Seq(
    chisel3.stage.ChiselGeneratorAnnotation(() => top),
    CIRCTTargetAnnotation(CIRCTTarget.Verilog)
  )

  val firtoolOptions = Seq(
    // FirtoolOption("--split-verilog"),                   // 生成多个文件
    // FirtoolOption("-o=build/verilog-gen"),

    // FirtoolOption("--lowering-options=noAlwaysComb"),   // 生成更低要求的verilog代码
    // FirtoolOption("--lowering-options=disallowLocalVariables"),
    // FirtoolOption("--lowering-options=disallowPackedArrays"),

    FirtoolOption("--disable-all-randomization")
  )

  val executeOptions = chiselStageOptions ++ firtoolOptions

  (new ChiselStage).execute(args, executeOptions)
}
