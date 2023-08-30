package space.nexel.aether.jvm.buffers

import space.nexel.aether.core.graphics.ShaderBuffer.Config

import java.nio.IntBuffer

class BufferI(size: Int) extends JvmBuffer {

  var buffer: IntBuffer = BufferUtil.newDirectIntBuffer(size)

  override def resizeBuffer(size: Int) = {
    val newBuffer = BufferUtil.newDirectIntBuffer(size)
    buffer.flip()
    newBuffer.put(buffer)
    buffer = newBuffer
  }

  override def getI(): Int = buffer.get()

  override def getF(): Float = buffer.get().toFloat

  override def putI(v: Int) = buffer.put(v)

  override def putF(v: Float): Unit = buffer.put(v.toInt)
  
  override def printElement(index: Int): String = buffer.get(index).toString

  // ---- DataStream
  // -- DataStream.Read
  override def readI(): Int = buffer.get()
  override def readL(): Long = (readI().toLong << 32) | readI()
  
  // -- DataStream.Write
  override def writeI(value: Int): Unit = buffer.put(value)
  override def writeL(value: Long): Unit = { writeI((value >> 32).toInt); writeI(value.toInt) }

}