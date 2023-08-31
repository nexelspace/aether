package space.nexel.aether.core.base

import space.nexel.aether.core.platform.Resource
import space.nexel.aether.core.buffers.ByteBuffer

case class Ref(base: Base, segments: Seq[String]) {

  def this(base: Base, path: String) = this(base, path.split('/'))

  def path: String = segments.mkString("/")
  def toUrl: String = base.toUrl(this.path)

  def loadString(): Resource[String] = base.loadString(this.path)
  def loadByteBuffer(): Resource[ByteBuffer] = base.loadByteBuffer(path)
  
}
