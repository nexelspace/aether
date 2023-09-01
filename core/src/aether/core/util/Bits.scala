package aether.core.util

object Bits {

  def byteOnes(byte: Int): Int = {
    var x = byte
    x -= (x >> 1) & 0x55
    x = ((x >> 2) & 0x33) + (x & 0x33)
    ((x >> 4) + x) & 0x0f
  }

  def byteIndexOfLSB(x: Int): Int = byteIndexOfMSB(x & -x)

  def byteIndexOfMSB(a: Int): Int = {
    var x = a & 0xff
    x |= x >> 1
    x |= x >> 2
    x |= x >> 4
    byteOnes(x) - 1
  }

  def ones(a: Int): Int = {
    var x = a
    x -= (x >> 1) & 0x55555555
    x = ((x >> 2) & 0x33333333) + (x & 0x33333333)
    x = ((x >> 4) + x) & 0x0f0f0f0f
    x += x >> 8
    x += x >> 16
    x & 0x0000003f
  }

  def onesL(a: Long): Int = {
    var x = a
    x -= (x >> 1) & 0x5555555555555555L
    x = ((x >> 2) & 0x3333333333333333L) + (x & 0x3333333333333333L)
    x = ((x >> 4) + x) & 0x0f0f0f0f0f0f0f0fL
    x += x >> 8
    x += x >> 16
    x += x >> 32
    x.toInt & 0x7f
  }

  def swapBytes(x: Int): Int = {
    (x << 24) & 0xff000000 | (x << 8) & 0xff0000 | (x >> 8) & 0xff00 | (x >> 24) & 0xff
  }

  def reverseBytes(a: Int): Int = {
    var x = a
    x = (x << 1) & 0xaaaaaaaa | (x >> 1) & 0x55555555
    x = (x << 2) & 0xcccccccc | (x >> 2) & 0x33333333
    (x << 4) & 0xf0f0f0f0 | (x >> 4) & 0x0f0f0f0f
  }

  def reverse(a: Int): Int = {
    var x = a
    x = (x << 1) & 0xaaaaaaaa | (x >> 1) & 0x55555555
    x = (x << 2) & 0xcccccccc | (x >> 2) & 0x33333333
    x = (x << 4) & 0xf0f0f0f0 | (x >> 4) & 0x0f0f0f0f
    x = (x << 8) & 0xff00ff00 | (x >> 8) & 0x00ff00ff
    (x << 16) | (x >>> 16)
  }

  def reverseL(a: Long): Long = {
    var x = a
    x = (x << 1) & 0xaaaaaaaaaaaaaaaaL | (x >> 1) & 0x5555555555555555L
    x = (x << 2) & 0xccccccccccccccccL | (x >> 2) & 0x3333333333333333L
    x = (x << 4) & 0xf0f0f0f0f0f0f0f0L | (x >> 4) & 0x0f0f0f0f0f0f0f0fL
    x = (x << 8) & 0xff00ff00ff00ff00L | (x >> 8) & 0x00ff00ff00ff00ffL
    x = (x << 16) & 0xffff0000ffff0000L | (x >> 16) & 0x0000ffff0000ffffL
    (x << 32) | (x >>> 32)
  }

  def indexOfLSB(x: Int): Int = indexOfMSB(x & -x)

  def indexOfMSB(a: Int): Int = {
    var x = a
    x |= x >> 1
    x |= x >> 2
    x |= x >> 4
    x |= x >> 8
    x |= x >> 16
    ones(x) - 1
  }

  def indexOfLSBL(x: Long): Int = indexOfMSBL(x & -x)

  def indexOfMSBL(a: Long): Int = {
    var x = a
    x |= x >> 1
    x |= x >> 2
    x |= x >> 4
    x |= x >> 8
    x |= x >> 16
    x |= x >> 32
    onesL(x) - 1
  }

  def numberOfLeadingZeros(x: Int): Int = 31 - indexOfMSB(x)

  def numberOfTrailingZeros(x: Int): Int = indexOfLSB(x)

  def parity(a: Int): Int = {
    var x = a
    x = x ^ (x >> 16)
    x = x ^ (x >> 8)
    x = x ^ (x >> 4)
    x = x ^ (x >> 2)
    x = x ^ (x >> 1)
    x & 1
  }

  def toString(x: Int, bits: Int) = {
    val a = new Array[Char](bits)
    for (i <- 0 until bits) {
      a(i) = if (((x >> i) & 1) == 0) '0' else '1'
    }
    new String(a)
  }
}
