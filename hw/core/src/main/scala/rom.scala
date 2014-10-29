package TurboRav

import Chisel._

import Common._
import Apb._

class Rom(implicit conf: TurboravConfig) extends Module {
  val io = new SlaveToApbIo()

  val s_idle :: s_setup :: s_access :: Nil = Enum(UInt(), 3)
  val state = Reg(init = s_idle)
  //Big ass statemachine, can't wait to refactor.
  when(state === s_idle)
  {
    when(io.sel) { state := s_setup }
  }
    .elsewhen(state === s_setup)
  {
    state := s_access
  }
    .elsewhen(state === s_access)
  {
    when(!io.ready)
    {
      state := s_access
    }
      .elsewhen(io.sel)
    {
      state := s_setup
    }
      .otherwise
    {
      state := s_idle
    }
  }

  val rom = Vec(
    Array(
      UInt(1, width=conf.apb_data_len),
      UInt(2, width=conf.apb_data_len),
      UInt(3, width=conf.apb_data_len),
      UInt(4, width=conf.apb_data_len)
    )
  )
  io.rdata  := rom(io.addr) & Fill(conf.apb_data_len, state === s_access)
  io.enable := state === s_access
  io.ready  := state === s_access
}
