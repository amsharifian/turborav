package TurboRav

import Chisel._
import Common._
import Constants._


/* The Rav V processor core */
class RavV (implicit conf: TurboravConfig) extends Module {

  val io = new Bundle(){
    val stall = Bool(INPUT)
  }
  val fch = Module(new Fetch())
  val dec = Module(new Decode())
  val exe = Module(new Execute())
  val mem = Module(new Memory())
  val wrb = Module(new Writeback())

  fch.io.fch_dec <> dec.io.fch_dec
  dec.io.dec_exe <> exe.io.dec_exe
  exe.io.exe_mem <> mem.io.exe_mem
  mem.io.mem_wrb <> wrb.io.mem_wrb
  wrb.io.wrb_dec <> dec.io.wrb_dec

}