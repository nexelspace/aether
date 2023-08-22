package space.nexel.aether.core.buffers

/**
 * Generic access to data buffer with pointers.
 * Data type conversion semantics depend on buffer implementations.
 * Based on java.nio.Buffer https://docs.oracle.com/javase/7/docs/api/java/nio/Buffer.html
 */
trait Buffer {

  def remaining: Int
  def position: Int
  def position_=(pos: Int): Unit
  def capacity: Int
  def limit: Int
  def flip(): Unit
  def clear(): Unit
  
  /** Get textual presentation of buffer element. */
  def printElement(index: Int): String

  def postWrite[T](length: Int)(preWriter: T => Unit)(postWriter: => T) = {
    val start = position
    position = position + length
    val pass = postWriter
    val end = position
    position = start
    preWriter(pass)
    assert(position == start + length, s"Invalid post write size ${position - start}, expected $length")
    position = end
  }
  
  override def toString(): String = {
    val out = new StringBuilder()
    out.append("[")
    out.append(position)
    out.append("/")
    out.append(limit)
    out.append("/")
    out.append(capacity)
    out.append("]")
    out.toString()
  }

  def toStringFull(): String = {
    val out = new StringBuilder()
    out.append("[")
    out.append(position)
    out.append("/")
    out.append(limit)
    out.append("/")
    out.append(capacity)
    out.append(":")
    var index = Math.max(0, position - 16)
    val end = Math.min(limit, index + 32)
    if (index > 0) out.append("..")
    while (index < end) {
      if (index > 0 && index == position) out.append("|")
      out.append(printElement(index))
      index += 1
      if (index < end) out.append(",")
    }
    if (index < limit) out.append("..")
    out.append("]")
    out.toString()
  }

    
}
