package space.nexel.aether.jvm.buffers

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer

object BufferUtil {

  def nativeOrder(buf: ByteBuffer): ByteBuffer = buf.order(ByteOrder.nativeOrder())

  def newDirectByteBuffer(size: Int): ByteBuffer =
    ByteBuffer.allocateDirect(size)

  def newDirectFloatBuffer(size: Int): FloatBuffer =
    nativeOrder(ByteBuffer.allocateDirect(4 * size)).asFloatBuffer()

  def newDirectShortBuffer(size: Int): ShortBuffer =
    nativeOrder(ByteBuffer.allocateDirect(2 * size)).asShortBuffer()

  def newDirectIntBuffer(size: Int): IntBuffer =
    nativeOrder(ByteBuffer.allocateDirect(4 * size)).asIntBuffer()

//  def toByteBuffer(buffer: ByteBuffer): ByteBuffer = {
//    buffer match {
//      case wrapped: WrappedBuffer => wrapped.buffer
//      case _ =>
//        val byteBuffer = ByteBuffer.allocate(buffer.remaining())
//        ???
//    }
//  }
}