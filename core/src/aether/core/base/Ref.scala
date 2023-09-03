package aether.core.base

import aether.core.platform.Resource
import aether.core.buffers.ByteBuffer

case class Ref(base: Base, segments: Seq[String]) {

  def this(base: Base, path: String) = this(base, path.split('/'))

  def path: String = segments.mkString("/")
  def toUrl: String = base.toUrl(this.path)

  def loadString() = base.loadString(this.path)
  // def loadTexture() = base.loadTexture(this.path)
  def loadBytes() = base.loadBytes(this.path)
  def loadJson() = base.loadJson(this.path)

  override def toString = toUrl

}
