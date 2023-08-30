package space.nexel.aether.jvm.buffers

import space.nexel.aether.core.buffers.NativeBuffer

import java.nio
import space.nexel.aether.core.types.Num

object JvmBuffer {
  def create(t: Num, size: Int) = t match {
    case Num.Byte  => new BufferB(size)
    case Num.UByte => new BufferB(size)
    case Num.Short => new BufferS(size)
    case Num.Int   => new BufferI(size)
    case Num.UInt  => new BufferI(size)
    case Num.Float => new BufferF(size)
    case dt          => sys.error(s"Unsupported buffer datatype $dt")
  }
}

/**
 * Native data buffer.
 */
trait JvmBuffer extends NativeBuffer {
  def buffer: nio.Buffer

  // ---- Buffer

  def clear(): Unit = buffer.clear()
  def compact(): Unit = ???
  def capacity: Int = buffer.capacity()
  def position: Int = buffer.position()
  def position_=(index: Int): Unit = buffer.position(index)
  def remaining: Int = buffer.remaining()
  def limit: Int = buffer.limit()
  def flip(): Unit = buffer.flip()

}
