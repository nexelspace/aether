package aether.jvm.buffers

import aether.core.buffers.DataBuffer
import java.io.BufferedInputStream
import java.io.DataInputStream

class ReadStream(in: DataInputStream) extends DataBuffer.Read {
  def read(array: Array[Byte], offset: Int, length: Int): Unit = in.read(array, offset, length)
  def readB(): Byte = in.readByte
  def readD(): Double = in.readDouble
  def readF(): Float = in.readFloat
  def readI(): Int = in.readInt
  def readL(): Long = in.readLong
  def readS(): Short = in.readShort
}
