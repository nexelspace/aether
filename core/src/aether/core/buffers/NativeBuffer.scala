package aether.core.buffers

import java.nio

/**
 * Native data buffer.
 */
trait NativeBuffer extends ElementBuffer with DataBuffer {

  def resizeBuffer(size: Int): Unit

  //  def getI(): Int
  //  def getF(): Float
  def getD(): Double = getF() //TODO: add BufferD

  //  def putI(v: Int)
  //  def putF(v: Float)
  def putD(v: Double) = putF(v.toFloat) //TODO: add BufferD

  // -- TODO: document
  // def put(v: Int): NativeBuffer = { putI(v); this }

  // ---- DataBuffer
  // -- DataBuffer.Read
  def read(array: Array[Byte], offset: Int, length: Int): Unit = notSupported
  def readB(): Byte = notSupported
  def readS(): Short = notSupported
  def readI(): Int = notSupported
  def readL(): Long = notSupported
  def readF(): Float = notSupported
  def readD(): Double = notSupported

  // -- DataBuffer.Write
  def write(array: Array[Byte], offset: Int, length: Int): Unit = notSupported
  def writeB(value: Int): Unit = notSupported
  def writeS(value: Int): Unit = notSupported
  def writeI(value: Int): Unit = notSupported
  def writeL(value: Long): Unit = notSupported
  def writeF(value: Float): Unit = notSupported
  def writeD(value: Double): Unit = notSupported

}
