package space.nexel.aether.core.buffers

/**
 * TODO: Use Scala 3 export
 */
trait BufferWrapper extends Buffer {

  def buffer: Buffer

  def remaining: Int = buffer.remaining
  def position: Int = buffer.position
  def capacity: Int = buffer.capacity
  def flip(): Unit = buffer.flip()
  def position_=(pos: Int): Unit = buffer.position = pos
  def limit: Int = buffer.limit
  def clear(): Unit = buffer.clear()

  def printElement(index: Int): String = buffer.printElement(index)

}
