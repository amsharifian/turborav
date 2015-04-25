package TurboRav

import Chisel._
import Common._
import Constants._

class Memory extends Module {
  val io = new MemoryIO()

  // Pipeline registers
  val exe_mem = Reg(init = new ExecuteMemory())

  val request  = io.rr_io.request
  val response = io.rr_io.response
  val mem_ctrl = exe_mem.mem_ctrl

  val is_mem_transfer = mem_ctrl.write || mem_ctrl.read

  request.valid        := is_mem_transfer
  request.bits.addr    := clearIfDisabled(exe_mem.alu_result, is_mem_transfer)
  request.bits.wdata   := clearIfDisabled(exe_mem.rs2, mem_ctrl.write)
  request.bits.write   := mem_ctrl.write
  request.bits.byte_en := Cat(mem_ctrl.is_halfword, mem_ctrl.is_byte)

  when(io.hdu_mem.stall) {
    io.mem_wrb.kill()
  } .otherwise {
    exe_mem := io.exe_mem
  }

  val word = response.bits.word(Config.xlen-1, 0)

  val sign_ext_halfword = Cat(Fill(word(15), Config.xlen-16), word(15, 0))
  val sign_ext_byte     = Cat(Fill(word( 7), Config.xlen- 8), word( 7, 0))

  val read_data =
    Mux(mem_ctrl.sign_extend && mem_ctrl.is_halfword, sign_ext_halfword,
    Mux(mem_ctrl.sign_extend && mem_ctrl.is_byte,     sign_ext_byte,
                                                     word))

  io.mem_wrb.mem_read_data := read_data
  io.mem_wrb <> exe_mem

  // Forwarding of ALU result
  io.mem_exe.alu_result := exe_mem.alu_result

  io.fwu_mem.rd_wen  := exe_mem.wrb_ctrl.rd_wen
  io.fwu_mem.rd_addr := exe_mem.rd_addr

  io.hdu_mem.mem_read := mem_ctrl.read
  io.hdu_mem.rd_addr  := exe_mem.rd_addr

}
