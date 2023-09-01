package aether.core.buffers

import aether.core.util.Strings

class DynamicBuffer(protected var buffer: Array[Byte]) extends ByteBuffer {
  val BYTE_MASK = 0xff
  protected var lim: Int = capacity
  protected var pos: Int = _

  def this(capacity: Int = 1) = {
    this(new Array[Byte](capacity))
  }

  def release() = {
  }

  def remaining: Int = lim - pos

  def capacity: Int = buffer.length

  def limit: Int = lim

  def position: Int = pos

  def flip() = {
    lim = pos
    pos = 0
  }

  def position_=(newPosition: Int) = {
    pos = newPosition
  }

  def clear() = {
    pos = 0
    lim = buffer.length
  }

  def compact() = {
    assert(pos >= 0 && pos <= lim && lim <= buffer.length, "Buffer corrupt: " + this)
    System.arraycopy(buffer, pos, buffer, 0, lim - pos)
    pos = lim - pos
    lim = buffer.length
  }

  def readB(): Byte = {
    assert(pos < lim, "Buffer overflow")
    pos += 1
    buffer(pos - 1)
  }
    
  def readB(index: Int): Byte = {
    assert(pos < lim, "Buffer overflow")
    buffer(index)
  }

  def writeB(value: Int) = {
    ensureSize(pos + 1)
    buffer(pos) = value.toByte
    pos += 1
  }

  def readS(): Short = {
    assert(pos + 1 < lim, "Buffer overflow")
    var result = (buffer(pos) & BYTE_MASK) << 8; pos += 1
    result |= buffer(pos) & BYTE_MASK; pos += 1
    result.toShort
  }

  def writeS(value: Int) = {
    ensureSize(pos + 2)
    buffer(pos) = (BYTE_MASK & (value >> 8)).toByte; pos += 1
    buffer(pos) = (BYTE_MASK & value).toByte; pos += 1
  }

  def readI(): Int = {
    assert(pos + 3 < lim, "Buffer overflow")
    var result = (buffer(pos) & BYTE_MASK) << 24; pos += 1
    result |= (buffer(pos) & BYTE_MASK) << 16; pos += 1
    result |= (buffer(pos) & BYTE_MASK) << 8; pos += 1
    result |= buffer(pos) & BYTE_MASK; pos += 1
    result
  }

  def readI(index: Int): Int = {
    assert(pos + 3 < lim, "Buffer overflow")
    var i = index
    var result = (buffer(i) & BYTE_MASK) << 24; i += 1
    result |= (buffer(i) & BYTE_MASK) << 16; i += 1
    result |= (buffer(i) & BYTE_MASK) << 8; i += 1
    result |= buffer(i) & BYTE_MASK; i += 1
    result
  }

  def writeI(value: Int) = {
    ensureSize(pos + 4)
    buffer(pos) = (BYTE_MASK & (value >> 24)).toByte; pos += 1
    buffer(pos) = (BYTE_MASK & (value >> 16)).toByte; pos += 1
    buffer(pos) = (BYTE_MASK & (value >> 8)).toByte; pos += 1
    buffer(pos) = (BYTE_MASK & value).toByte; pos += 1
  }

  def readL(): Long = {
    var value: Long = 0
    var i = 8
    while (i >= 0) {
      value <<= 8
      value |= buffer(pos) & BYTE_MASK
      pos += 1
    }
    value
  }

  def writeL(value: Long) = {
    var v = value
    ensureSize(pos + 8)
    var i = 8
    while (i >= 0) {
      buffer(pos + i) = (BYTE_MASK & value).toByte
      v >>= 8
    }
    pos += 8
  }

  def readF(): Float = {
    java.lang.Float.intBitsToFloat(readI())
  }

  def writeF(value: Float) = {
    val bits = java.lang.Float.floatToIntBits(value)
    writeI(bits)
  }

  def readD(): Double = {
    java.lang.Double.longBitsToDouble(readL())
  }

  def writeD(value: Double) = {
    val bits = java.lang.Double.doubleToLongBits(value)
    writeL(bits)
  }

  def read(array: Array[Byte], offset: Int, length: Int) = {
    assert(offset + length <= pos + array.length, "Get " + offset + " + " + length + " from " + this)
    System.arraycopy(buffer, pos, array, offset, length)
    pos += length
  }

  def write(array: Array[Byte], offset: Int, length: Int) = {
    ensureSize(pos + length)
    System.arraycopy(array, offset, buffer, pos, length)
    pos += length
  }

  override def printElement(index: Int): String = Strings.toHex(buffer(index), 2)

  def readArray(): Array[Byte] = {
    val result = Array.ofDim[Byte](remaining)
    read(result, 0, remaining)
    result
  }

  def getInternalBuffer(requiredSize: Int): Array[Byte] = {
    ensureSize(requiredSize)
    buffer
  }

  protected def ensureSize(size: Int) = {
    if (lim < size) {
      lim = size
    }
    var capacity = buffer.length
    assert(size >= 0, "Invalid size: " + capacity + ", " + size)
    assert(capacity > 0, "Invalid capacity: " + capacity + ", " + size)
    if (capacity < size) {
      while (capacity < size) capacity <<= 1
      val newBuffer = Array.ofDim[Byte](capacity)
      System.arraycopy(buffer, 0, newBuffer, 0, buffer.length)
      buffer = newBuffer
    }
  }
}













