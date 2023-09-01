package aether.core.buffers

import aether.core.util.Bits

object Buffers {

  def byteGammaBytes(value: Int) = Bits.indexOfMSB(value) / 7 + 1
  
  /**
   * Put integer in Byte Aligned Elias Gamma variation format.
   * @param buffer
   * @param value
   */
  def writeByteGamma(buffer: DataBuffer.Write, value: Int) = {
    assert(value >= 0, s"Negative values not allowed: $value")
    var size = Bits.indexOfMSB(value) / 7
    while (size > 0) {
      buffer.writeB(((value >> (size * 7)) & 0x7f) | 0x80)
      size -= 1
    }
    buffer.writeB(value & 0x7f)
  }

  def writeByteGammaL(buffer: DataBuffer.Write, value: Long) = {
    assert(value >= 0, s"Negative values not allowed: $value")
    var size = Bits.indexOfMSBL(value) / 7
    while (size > 0) {
      buffer.writeB(((value >> (size * 7)).toInt & 0x7f) | 0x80)
      size -= 1
    }
    buffer.writeB(value.toInt & 0x7f)
  }

  /**
   * Get integer in Byte Aligned Elias Gamma variation format.
   * @param buffer
   * @return
   */
  def readByteGamma(buffer: DataBuffer.Read): Int = {
    var result = 0
    while (true) {
      var b = buffer.readUB()
      if (b < 0x80) return result | b
      result = (result | (b & 0x7f)) << 7
    }
    ???
  }

  def readByteGammaL(buffer: DataBuffer.Read): Long = {
    var result = 0L
    while (true) {
      val b = buffer.readUB()
      if (b < 0x80) return result | b
      result = (result | (b & 0x7f)) << 7
    }
    ???
  }

  def skipByteGamma(buffer: DataBuffer) = {
    while (buffer.readUB() >= 0x80) {}
  }

  def skipByteGamma(buffer: ByteBuffer, count: Int) = {
    for (i <- 0 until count) while (buffer.readUB() >= 0x80) {}
  }

  // ---- Real

  def encodeSigned(value: Int): Int = {
    return if (value < 0) ~(value << 1) else (value << 1)
  }

  def encodeSignedL(value: Long): Long = {
    return if (value < 0) ~(value << 1) else (value << 1)
  }

  def decodeSigned(symbol: Int): Int = {
    assert(symbol >= 0, s"Negative symbol: $symbol")
    return if ((symbol & 1) == 0) symbol >> 1 else ~(symbol >> 1)
  }

  def decodeSignedL(symbol: Int): Long = {
    assert(symbol >= 0, s"Negative symbol: $symbol")
    return if ((symbol & 1) == 0) symbol >> 1 else ~(symbol >> 1)
  }

  def readRealF(buffer: ByteBuffer): Float = {
    var exponent = readByteGamma(buffer)
    val mantissa = readByteGamma(buffer)
    assert(exponent < 0x200, s"Exponent overflow: $exponent")
    if (mantissa == 0) {
      // special numbers
      if (exponent == 0) {
        return 0;
      } else if (exponent == 1) {
        return Float.NegativeInfinity
      } else if (exponent == 2) {
        return Float.PositiveInfinity
      } else if (exponent == 3) {
        return Float.NaN
      }
      assert(false, s"Invalid real values: $exponent, $mantissa")
      return 0
    }
    val fraction = Bits.reverse(mantissa - 1)
    exponent = decodeSigned(exponent) + 127
    var bits = fraction & 0x80000000 // sign
    bits |= exponent << 23 // exponent
    bits |= (fraction >>> 8) & 0x7fffff // mantissa
    return java.lang.Float.intBitsToFloat(bits);
  }

  def readRealD(buffer: ByteBuffer): Double = {
    var exponent = readByteGamma(buffer);
    val mantissa = readByteGammaL(buffer);
    assert(exponent < 0x1000, s"Exponent overflow: $exponent")
    if (mantissa == 0) {
      // special numbers
      if (exponent == 0) {
        return 0
      } else if (exponent == 1) {
        return Double.NegativeInfinity
      } else if (exponent == 2) {
        return Double.PositiveInfinity
      } else if (exponent == 3) {
        return Double.NaN
      }
      assert(false, "Invalid real values: $exponent, $mantissa")
      return 0
    }
    val fraction = Bits.reverseL(mantissa - 1)
    exponent = decodeSigned(exponent) + 1023
    var bits = fraction & 0x8000000000000000L // sign
    bits |= exponent.toLong << 52 // exponent
    bits |= (fraction >>> 11) & 0xfffffffffffffL // mantissa
    return java.lang.Double.longBitsToDouble(bits)
  }

  def writeRealF(buffer: ByteBuffer, value: Float) = {
    if (value == 0) {
      buffer.writeB(0);
      buffer.writeB(0);
    } else if (value.isNaN()) {
      buffer.writeB(3);
      buffer.writeB(0);
    } else if (value.isInfinite()) {
      buffer.writeB(if (value < 0) 1 else 2)
      buffer.writeB(0);
    } else {
      val bits = java.lang.Float.floatToIntBits(value)
      val e = (bits >> 23) & 0xff
      writeByteGamma(buffer, encodeSigned(e - 127))
      val fraction = Bits.reverse((bits & 0x80000000) | ((bits & 0x7fffff) << 8))
      writeByteGamma(buffer, fraction + 1)
    }
  }

  def writeRealD(buffer: ByteBuffer, value: Double) = {
    if (value == 0) {
      buffer.writeB(0)
      buffer.writeB(0)
    } else if (value.isNaN()) {
      buffer.writeB(3)
      buffer.writeB(0)
    } else if (value.isInfinite()) {
      buffer.writeB(if (value < 0) 1 else 2)
      buffer.writeB(0)
    } else {
      val bits = java.lang.Double.doubleToLongBits(value)
      val e = (bits >> 52).toInt & 0x7ff
      writeByteGamma(buffer, encodeSigned(e - 1023))
      val fraction = Bits.reverseL((bits & 0x8000000000000000L) | ((bits & 0xfffffffffffffL) << 11))
      writeByteGammaL(buffer, fraction + 1)
    }
  }

  // ---- UTF-8

  def readUTF8(buffer: ByteBuffer, length: Int): String = {
    val out = new StringBuilder()
    while (buffer.remaining > 0) {
      out += (buffer.readUB() match {
        case c if ((c & 0x80) == 0)    => c
        case c if ((c & 0xe0) == 0xc0) => ((c & 0x1f) << 6) | (buffer.readUB() & 0x3f)
        case c if ((c & 0xf0) == 0xe0) => ((c & 0x0f) << 12) | ((buffer.readUB() & 0x3f) << 6) | ((buffer.readUB() & 0x3f))
        case e => sys.error(s"Unexpected code "+e.toHexString)
      }).toChar
    }
    out.toString
  }

  def readChar(buffer: ByteBuffer): Char = readChar(buffer, buffer.readUB())

  def readChar(buffer: ByteBuffer, firstByte: Int): Char = {
    (firstByte match {
      case c if ((c & 0x80) == 0)    => c
      case c if ((c & 0xe0) == 0xc0) => ((c & 0x1f) << 6) | (buffer.readUB() & 0x3f)
      case c if ((c & 0xf0) == 0xe0) => ((c & 0x0f) << 12) | ((buffer.readUB() & 0x3f) << 6) | ((buffer.readUB() & 0x3f))
    }).toChar
  }

  def readUTF8Terminated(buffer: ByteBuffer): String = {
    val out = new StringBuilder()
    while (true) {
      out += (buffer.readUB() match {
        case 0 => return out.toString()
        case c => readChar(buffer, c)
      })
    }
    ???
  }

  def writeChar(buffer: ByteBuffer, char: Char) = {
    if (char < 0x80) {
      buffer.writeB(char)
    } else if (char < 0x800) {
      buffer.writeB(0xc0 | (char >> 6))
      buffer.writeB(char & 0x3f)
    } else {
      buffer.writeB(0xe0 | (char >> 12))
      buffer.writeB((char >> 6) & 0x3f)
      buffer.writeB(char & 0x3f)
    }
  }

  def writeUTF8(buffer: ByteBuffer, string: String): Unit = {
    string.foreach(writeChar(buffer, _))
  }

  def writeUTF8Terminated(buffer: ByteBuffer, string: String): Unit = {
    string.foreach(writeChar(buffer, _))
    buffer.writeB(0)
  }
  


}