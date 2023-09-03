package aether.lib.codec

import aether.core.buffers.ByteBuffer

// import scala.reflect.ClassTag
// import scala.reflect._

trait Codec[T](val version: Int) {
  // def codecClass = classTag[T].runtimeClass
  def decode(buffer: ByteBuffer): T
  def encode(buffer: ByteBuffer, obj: T): Unit
}
