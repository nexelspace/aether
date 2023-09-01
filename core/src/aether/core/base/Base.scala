package aether.core.base

import aether.core.platform.Resource
import aether.core.buffers.ByteBuffer
import aether.core.graphics.Texture
import io.circe.Json
import Base.*

object Base {
  
  enum Entry(name: String) {
    case Dir(name: String) extends Entry(name)
    case File(name: String) extends Entry(name)
  }
}

trait Base {
  def ref(path: String): Ref = new Ref(this, path)
  def toUrl(path: String): String

  def base(path: String): Base
  def entry(path: String): Resource[Entry]
  def list(pathDir: String): Resource[Seq[Entry]]

  def loadString(path: String): Resource[String]
  // def loadTexture(path: String): Resource[Texture]
  def loadBytes(path: String): Resource[Array[Byte]]
  def loadJson(path: String): Resource[Json]
  
}
