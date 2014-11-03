package Common
{
  case class TurboravConfig
  {
    val xlen     = 32
    val apb_addr_len = 32
    val apb_data_len = 32
    val rom_contents_path = "resources/rom_contents.hex"
    val cache = new CacheConfig()
  }

  case class CacheConfig
  {
    val cacheLineWidth = 128
    val numEntries     = 128
    val associativity  = 1
  }
}