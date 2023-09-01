package aether.core.buffers

import aether.core.util.Strings

object ByteBuffer {

  /** Create fixed size buffer. */
  def apply(size: Int): ByteBuffer = new DynamicBuffer(size)
  /** Create dynamic buffer. */
  def apply(): ByteBuffer = new DynamicBuffer()

  def apply(bytes: Array[Byte]): ByteBuffer = new DynamicBuffer(bytes)
  
}

/**
 * Base class for Byte buffers.
 */
abstract class ByteBuffer extends DataBuffer with DataBuffer.Read with DataBuffer.Write {

  /** Get byte at specific buffer index. */
  def readB(index: Int): Byte

  def readString(): String = {
    //assert(isAvailable)
    val length = remaining
    val array = new Array[Byte](length)
    read(array, 0, length)
    new String(array, "UTF8")
  }

  def postInject[T](preWriter: (ByteBuffer, Int, T) => Unit)(postWriter: ByteBuffer => T) = {
    val postBuffer = ByteBuffer()
    val pass = postWriter(postBuffer)
    postBuffer.flip()
    val size = postBuffer.remaining
    preWriter(this, size, pass)
    write(postBuffer, size)
  }
  
  def toByteArray: Array[Byte] = {
    val array = new Array[Byte](remaining)
    read(array, 0, remaining)
    array
  }
  
  def printElement(index: Int): String = Strings.toHex(readB(index), 2)

}

