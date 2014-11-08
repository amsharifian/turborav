package TurboRav

import Chisel._
import Common._
import Constants._

class FetchDecode() extends Bundle {
  val instr_valid = Bool(OUTPUT)
  val instr       = UInt(OUTPUT, INSTRUCTION_WIDTH)
  def reset(){
    instr_valid := Bool(false)
    instr := UInt(0)
  }
}

class DecodeExecute() extends Bundle {
  val rs1     = UInt(OUTPUT, Config.xlen)
  val rs2     = UInt(OUTPUT, Config.xlen)
  val imm     = UInt(OUTPUT, Config.xlen)
  val rd_addr = UInt(OUTPUT, Config.xlen)

  val exe_ctrl = new ExecuteCtrl()
  val mem_ctrl = new MemoryCtrl()
  val wrb_ctrl = new WritebackCtrl()
}

class ExecuteMemory() extends Bundle {
  val rd_addr    = UInt(OUTPUT, Config.xlen)
  val alu_result = UInt(OUTPUT, Config.xlen)
  val rs2        = UInt(OUTPUT, Config.xlen)

  val mem_ctrl = new MemoryCtrl()
  val wrb_ctrl = new WritebackCtrl()
}

class MemoryWriteback() extends Bundle {
  val rd_addr       = UInt(OUTPUT, Config.xlen)
  val alu_result    = UInt(OUTPUT, Config.xlen)
  val mem_read_data = UInt(OUTPUT, Config.xlen)

  val wrb_ctrl = new WritebackCtrl()
}

class WritebackDecode() extends Bundle {
  val rd_wen  = Bool(OUTPUT)
  val rd_data = UInt(OUTPUT, Config.xlen)
  val rd_addr = UInt(OUTPUT, 5)
}

class ExecuteCtrl() extends Bundle {
  val alu_in_a_sel = Bits(OUTPUT, 2)
  val alu_in_b_sel = Bits(OUTPUT, 2)
  val alu_func     = Bits(OUTPUT, ALU_FUNC_WIDTH)
}

class MemoryCtrl() extends Bundle {
  val write = Bool(OUTPUT)
  val read = Bool(OUTPUT)
}

class WritebackCtrl() extends Bundle {
  val rd_wen         = Bool(OUTPUT)
  val regbank_in_sel = Bits(OUTPUT, 2)
}

class FetchIO() extends Bundle {
  val fch_dec = new FetchDecode()
}

class DecodeIO() extends Bundle {
  val fch_dec = new FetchDecode().flip()
  val dec_exe = new DecodeExecute()
  val wrb_dec = new WritebackDecode().flip()

  //TODO: this one should be in HDU interface or something similar.
  val stall = Bool(INPUT)
}

class ExecuteIO() extends Bundle {
  val dec_exe = new DecodeExecute().flip()
  val exe_mem = new ExecuteMemory()

  val stall = Bool(INPUT)
}

class MemoryIO() extends Bundle {
  val exe_mem = new ExecuteMemory().flip()
  val mem_wrb = new MemoryWriteback()

  val stall = Bool(INPUT)
}

class WritebackIO() extends Bundle {
  val mem_wrb = new MemoryWriteback().flip()
  val wrb_dec = new WritebackDecode()

  val stall = Bool(INPUT)
}
