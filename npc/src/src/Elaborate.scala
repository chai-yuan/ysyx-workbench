import core.CPUTop
import circt.stage._

object Elaborate extends App {
  def top       = new CPUTop()
  val generator = Seq(chisel3.stage.ChiselGeneratorAnnotation(() => top))
  (new ChiselStage).execute(args, generator :+ CIRCTTargetAnnotation(CIRCTTarget.Verilog))
}
