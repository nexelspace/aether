package space.nexel.aether.core.base

import space.nexel.aether.core.platform.Resource
import space.nexel.aether.core.buffers.ByteBuffer
import space.nexel.aether.core.graphics.Texture

trait Base {
  def ref(path: String): Ref = new Ref(this, path)

  def loadString(path: String): Resource[String]
  def loadTexture(path: String): Resource[Texture]
  def loadByteBuffer(path: String): Resource[ByteBuffer]
  // def load[Array[Byte]](path: String): Resource[Array[Byte]]
}
