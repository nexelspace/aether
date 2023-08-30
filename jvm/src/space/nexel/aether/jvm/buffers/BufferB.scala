package space.nexel.aether.jvm.buffers

import java.nio.ByteBuffer

class BufferB(size: Int) extends JvmBuffer {

  var buffer: ByteBuffer = BufferUtil.newDirectByteBuffer(size)

  override def resizeBuffer(size: Int) = {
    val newBuffer = BufferUtil.newDirectByteBuffer(size)
    buffer.flip()
    newBuffer.put(buffer)
    buffer = newBuffer
  }

  override def getI(): Int = buffer.get()

  override def getF(): Float = buffer.get()

  override def putI(v: Int) = buffer.put(v.toByte)

  override def putF(v: Float): Unit = buffer.put(v.toByte)

  override def printElement(index: Int): String = buffer.get(index).toString

  // -- DataStream.Read
  override def read(array: Array[Byte], offset: Int, length: Int): Unit = {
    buffer.get(array, offset, length)
  }
  override def readB(): Byte = buffer.get
  override def readS(): Short = ((readB() << 8) | readB()).toShort
  override def readI(): Int = (readS() << 16) | readS()
  override def readL(): Long = (readI().toLong << 32) | readI()

  // -- DataStream.Write
  override def write(array: Array[Byte], offset: Int, length: Int): Unit = {
    buffer.put(array, offset, length)
  }
  override def writeB(value: Int): Unit = buffer.put(value.toByte)
  override def writeS(value: Int): Unit = { writeB(value >> 8); writeB(value) }
  override def writeI(value: Int): Unit = { writeS(value >> 16); writeS(value) }
  override def writeL(value: Long): Unit = { writeI((value >> 32).toInt); writeI(value.toInt) }


}