package TurboRav

import Chisel._
import Constants._
import Common._

class Cache(implicit conf: TurboravConfig) extends Module {
  //TODO: declaration order of io and require statements is
  //inconsistent between Cache and Alu. What is best? io I guess.
    val io = new Bundle {
      val address = UInt(INPUT, 32)
      val data    = UInt(OUTPUT, 32)
    }

  // Extract configuration for convenience
  val cacheLineWidth = conf.cache.cacheLineWidth
  val numEntries     = conf.cache.numEntries
  val associativity  = conf.cache.associativity

    val INSTRUCTION_WIDTH_UINT = UInt(INSTRUCTION_WIDTH)

    require(isPow2(numEntries))
    require(numEntries % associativity == 0)
    require(cacheLineWidth % INSTRUCTION_WIDTH == 0)

    val entriesPerBank = numEntries / associativity
    val instrPerCacheLine = cacheLineWidth / INSTRUCTION_WIDTH

    val dataBanks = Array.fill(associativity) {
      Mem(UInt(width = cacheLineWidth), entriesPerBank)
    }

    val numOffsetBits = log2Down(cacheLineWidth / INSTRUCTION_WIDTH)
    val numIndexBits  = log2Down(entriesPerBank)
    val numTagBits    = INSTRUCTION_WIDTH - numOffsetBits - numIndexBits

    val tagBanks = Array.fill(associativity) {
      new Bundle {
        val isValid = Mem(Bool(false),              entriesPerBank)
        val bank    = Mem(UInt(width = numTagBits), entriesPerBank)
      }
    }

    val instrOffset = io.address(numOffsetBits - 1, 0)
    val instrIndex  = io.address(numOffsetBits + numIndexBits - 1, numOffsetBits)
    val instrTag    = io.address(numOffsetBits + numIndexBits + numTagBits - 1,
      numOffsetBits + numIndexBits)

    /*
    when (tagBanks(0).bank(instrIndex) === instrTag
      && tagBanks(0).isValid(instrIndex)) {

    }
    */

    val muxArray = Array.ofDim[(UInt, UInt)](instrPerCacheLine)
    for (i <- 0 until instrPerCacheLine) {
      val upperInstrBitIndex = i * (INSTRUCTION_WIDTH + 1) - 1
      val lowerInstrBitIndex = i * INSTRUCTION_WIDTH - 1
      muxArray(i) = (UInt(i), dataBanks(0)(instrIndex)(upperInstrBitIndex, lowerInstrBitIndex))
    }


    io.data := Lookup(
      instrOffset,
      UInt(0, 32),
      muxArray
    )

  }