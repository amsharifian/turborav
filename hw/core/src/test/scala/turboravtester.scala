package TurboRav

import Chisel._
import Common._

object TurboRavTestRunner{
  def main(args: Array[String]): Unit = {
    val Array(module, rom) = args
    val test_args = Array(
      "--genHarness",
	  "--backend", "c",
	  "--targetDir", "generated/%s" format rom,
	  "--compile",
	  "--test",
	  "--vcd",
	  "--debug"
    )

    // En pils til førstemann som kan fjerne redundansen.
    // Hvis du prøvde og feilet inkrementer følgende:
    // 2
    val res = module match {
      case "alutest" =>
        chiselMainTest(test_args, () => Module(new Alu())){
          c => new AluTest(c)
        }
      case "brutest" =>
        chiselMainTest(test_args, () => Module(new BranchUnit())){
          c => new BranchUnitTest(c)
        }
      case "fwutest" =>
        chiselMainTest(test_args, () => Module(new ForwardingUnit())){
          c => new ForwardingUnitTest(c)
        }
      case "regbanktest" =>
        chiselMainTest(test_args, () => Module(new RegBank())){
          c => new RegBankTest(c)
        }
      case "multtest" =>
        chiselMainTest(test_args, () => Module(new Mult())){
          c => new MultTest(c)
        }
      case "decodetest" =>
        chiselMainTest(test_args, () => Module(new Decode())){
          c => new DecodeTest(c)
        }
      case "executetest" =>
        chiselMainTest(test_args, () => Module(new Execute())){
          c => new ExecuteTest(c)
        }
      case "memorytest" =>
        chiselMainTest(test_args, () => Module(new Memory())){
          c => new MemoryTest(c)
        }
      case "writebacktest" =>
      chiselMainTest(test_args, () => Module(new Writeback())){
        c => new WritebackTest(c)
      }
      case "ravvtest" =>
        chiselMainTest(test_args, () => Module(new RavV())){
          c => new RavVTest(c)
        }
      case "soctest" =>
        chiselMainTest(test_args, () => Module(new Soc())){
          c => new SocTest(c)
        }
      case "riscvtest" =>
        chiselMainTest(
          test_args,
          () => Module(new Soc())
        )
        {
          c => new RiscvTest(c, rom)
        }
    }
  }
}
