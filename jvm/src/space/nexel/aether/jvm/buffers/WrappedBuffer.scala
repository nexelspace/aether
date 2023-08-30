package space.nexel.aether.jvm.buffers

// import space.nexel.aether.core.buffers.AbstractBuffer

import java.nio.ByteBuffer
import space.nexel.aether.core.buffers

object WrappedBuffer {
  def apply(size: Int): WrappedBuffer = new WrappedBuffer(ByteBuffer.allocate(size))
  def apply(buffer: ByteBuffer): WrappedBuffer = new WrappedBuffer(buffer)
}

class WrappedBuffer(val buffer: ByteBuffer) extends buffers.ByteBuffer {

  def release() = {}

  def remaining: Int = buffer.remaining()
  def capacity: Int = buffer.capacity()
  def limit: Int = buffer.limit()
  def position: Int = buffer.position
  def flip() = buffer.flip()
  def position_=(newPosition: Int) = buffer.position(newPosition)
  def clear() = buffer.clear()
  def compact() = buffer.clear()

  def readB(index: Int): Byte = buffer.get(index)

  def readB(): Byte = buffer.get()
  def writeB(value: Int) = buffer.put(value.toByte)

  def readS(): Short = buffer.getShort()
  def writeS(value: Int) = buffer.putShort(value.toShort)

  def readI(): Int = buffer.getInt()
  def writeI(value: Int) = buffer.putInt(value)

  def readL(): Long = buffer.getLong()
  def writeL(value: Long) = buffer.putLong(value)

  def readF(): Float = buffer.getFloat()
  def writeF(value: Float) = buffer.putFloat(value)

  def readD(): Double = buffer.getDouble()
  def writeD(value: Double) = buffer.putDouble(value)

  def read(array: Array[Byte], offset: Int, length: Int) = buffer.get(array, offset, length)
  def write(array: Array[Byte], offset: Int, length: Int) = buffer.put(array, offset, length)

}













