package aether.jvm.buffers

import aether.core.buffers.Buffers
import aether.core.graphics.ShaderBuffer.Config
import aether.core.types.Vec2F
import aether.core.types.Vec3F
import aether.core.types.Vec4F

import java.nio.FloatBuffer
import java.nio.IntBuffer

class BufferF(size: Int) extends JvmBuffer {

  var buffer: FloatBuffer = BufferUtil.newDirectFloatBuffer(size)

  def resizeBuffer(size: Int) = {
    val newBuffer = BufferUtil.newDirectFloatBuffer(size)
    buffer.flip()
    newBuffer.put(buffer)
    buffer = newBuffer
  }

  override def getI(): Int = buffer.get().toInt

  override def getF(): Float = buffer.get()

  override def putI(v: Int) = buffer.put(v.toFloat)

  override def putF(v: Float): Unit = buffer.put(v)
  
  override def printElement(index: Int): String = "%.1f".format(buffer.get(index))

  // ---- DataStream
  // -- DataStream.Read
  override def read(array: Array[Byte], offset: Int, length: Int): Unit = notSupported
//  override def readB(): Byte = notSupported
//  override def readS(): Short = notSupported
//  override def readI(): Int = notSupported

  // -- DataStream.Write
  override def write(array: Array[Byte], offset: Int, length: Int): Unit = notSupported
//  override def writeB(value: Int): Unit = notSupported
//  override def writeS(value: Int): Unit = notSupported
//  override def writeI(value: Int): Unit = notSupported
//  override def writeL(value: Long): Unit = notSupported
}
