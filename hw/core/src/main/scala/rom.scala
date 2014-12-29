package TurboRav

import Chisel._

import Common._
import Array._
import Apb._

class Rom() extends Module {
  val io = new SlaveToApbIo()

  // This seems to be a common pattern, but I need a better name for
  // it I think. Surely a name for something this generic should
  // already exist.
  def clearIfDisabled(data: UInt, enabled: Bool):UInt = {
    data & Fill(enabled, data.getWidth())
  }

  // Hardcoding until I am able to read from file correctly. The
  // machine-code hex translation is from core/riscv_test_code
  val rom_array = Array(
    0x00100293, // li	x5,1
    0x00200213, // li	x4,2
    0x005201b3, // add	x3,x4,x5
    0x00000063  // b	c <main+0xc> //Should jump to 0
  )

  // The apb bus addresses individual bytes, but the ROM stores 4-byte
  // words and assumes that all addresses are word-aligned. To go from
  // a byte-addressable address to a word addressable address we
  // right-shift twice.
  val word_addr = io.addr >> UInt(2)
  assert(io.addr(1,0) === UInt(0), "We assume word-aligned addresses.")

  val rom = Vec(rom_array.map(UInt(_)))

  io.rdata  := clearIfDisabled(
    data = rom(Reg(next = word_addr)),
    enabled = io.enable
  )

  val s_idle :: s_ready :: Nil = Enum(UInt(), 2)
  val state = Reg(init = s_idle)

  when( state === s_ready ){
    state := s_idle
  } .elsewhen ( io.sel ) {
    state := s_ready
  } .otherwise {
    state := s_idle
  }

  io.ready  := state === s_ready
  io.enable := io.ready
}
