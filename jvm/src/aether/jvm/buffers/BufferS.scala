package aether.jvm.buffers

import aether.core.graphics.ShaderBuffer.Config

import java.nio.ShortBuffer

class BufferS(size: Int) extends JvmBuffer {

  var buffer: ShortBuffer = BufferUtil.newDirectShortBuffer(size)

  override def resizeBuffer(size: Int) = {
    val newBuffer = BufferUtil.newDirectShortBuffer(size)
    buffer.flip()
    newBuffer.put(buffer)
    buffer = newBuffer
  }

  override def getI(): Int = buffer.get()

  override def getF(): Float = buffer.get()

  override def putI(v: Int) = buffer.put(v.toShort)

  override def putF(v: Float): Unit = buffer.put(v.toShort)
  
  override def printElement(index: Int): String = buffer.get(index).toString

    // ---- DataStream
  // -- DataStream.Read
  override def readS(): Short = buffer.get()
  override def readI(): Int = (readS() << 16) | readS()
  override def readL(): Long = (readI().toLong << 32) | readI()

  // -- DataStream.Write
  override def writeS(value: Int): Unit = buffer.put(value.toShort)
  override def writeI(value: Int): Unit = { writeS(value >> 16); writeS(value) }
  override def writeL(value: Long): Unit = { writeI((value >> 32).toInt); writeI(value.toInt) }
  
}