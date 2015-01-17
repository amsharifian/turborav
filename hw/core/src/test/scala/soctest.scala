package TurboRav

import Chisel._
import Constants._

class SocTest(c: Soc) extends Tester(c) {
  step(200)
  expect(c.ravv.exe.io.exe_mem.alu_result, 3)
}
