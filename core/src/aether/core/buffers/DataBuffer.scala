package aether.core.buffers

/**
 * Buffer with data read/write support.
 * Read & write methods may apply to multiple buffer elements.
 * Complex data types are decoded/encoded.
 */
object DataBuffer {
  val BYTE_MASK = 0xff

  trait Read {

    /** Read unsigned byte as Int. */
    final def readUB(): Int = readB() & BYTE_MASK

    def readB(): Byte
    def readS(): Short
    def readI(): Int
    def readL(): Long
    def readF(): Float
    def readD(): Double
    def readVI(): Int = {
      Buffers.readByteGamma(this)
    }
    def readVL(): Long = {
      Buffers.readByteGammaL(this)
    }

    def read(array: Array[Byte], offset: Int, length: Int): Unit

  }

  trait Write {

    def writeB(value: Int): Unit
    def writeS(value: Int): Unit
    def writeI(value: Int): Unit
    def writeL(value: Long): Unit
    def writeF(value: Float): Unit
    def writeD(value: Double): Unit
    def writeVI(value: Int): Unit = {
      Buffers.writeByteGamma(this, value)
    }
    def writeVL(value: Long): Unit = {
      Buffers.writeByteGammaL(this, value)
    }

    def write(array: Array[Byte], offset: Int, length: Int): Unit
    def write(array: Array[Byte]): Unit = write(array, 0, array.length)

  }

  trait IO extends Read with Write {
    def notSupported = sys.error("Buffer read/write operation is not supported for this data type: "+getClass.getSimpleName)
  }
}

trait DataBuffer extends Buffer with DataBuffer.IO {

  /** Write bytes to this buffer. */
  // REFACTOR rename to writeB?
  def write(source: DataBuffer, length: Int): Int = {
    for (i <- 0 until length) writeB(source.readB())
    length
  }
}
