package TurboRav

import Chisel._
import Constants._

/* The Rav V processor core */
class RavV(elf_path: String, fpga: Boolean) extends Module {
  val io = new RequestResponseIo()

  val fch  = Module(new Fetch())
  val dec  = Module(new Decode())
  val exe  = Module(new Execute())
  val mem  = Module(new Memory())
  val wrb  = Module(new Writeback())
  val fwu  = Module(new ForwardingUnit())
  val hdu  = Module(new HazardDetectionUnit())

  fch.io.fch_dec <> dec.io.fch_dec
  dec.io.dec_exe <> exe.io.dec_exe
  exe.io.exe_mem <> mem.io.exe_mem
  mem.io.mem_wrb <> wrb.io.mem_wrb

  fwu.io.fwu_exe <> exe.io.fwu_exe
  fwu.io.fwu_mem <> mem.io.fwu_mem
  fwu.io.fwu_wrb <> wrb.io.fwu_wrb

  exe.io.mem_exe <> mem.io.mem_exe
  exe.io.wrb_exe <> wrb.io.wrb_exe
  fch.io.exe_fch <> exe.io.exe_fch

  fch.io.hdu_fch <> hdu.io.hdu_fch
  dec.io.hdu_dec <> hdu.io.hdu_dec
  exe.io.hdu_exe <> hdu.io.hdu_exe
  mem.io.hdu_mem <> hdu.io.hdu_mem
  wrb.io.hdu_wrb <> hdu.io.hdu_wrb

  // Roam
  val rom = Module(new Rom(elf_path))
  val ram = Module(new FpgaRam())

  val wishbone_masters = List(fch, mem)
  val wishbone_slaves = List(rom, ram)
  wishbone.WishboneInterconnection(
    wishbone.Crossbar,
    wishbone_masters,
    wishbone_slaves
  )
}
