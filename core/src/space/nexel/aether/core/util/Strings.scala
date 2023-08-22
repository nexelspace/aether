package space.nexel.aether.core.util

object Strings {
  val HEX = "0123456789abcdef"
  def toHex(value: Int, digits: Int): String = {
    var v = value
    val c = new Array[Char](digits)
    for (i <- (digits - 1) to 0 by -1) {
      c(i) = HEX(v & 0xf)
      v >>= 4
    }
    new String(c)
  }
}
