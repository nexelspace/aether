package space.nexel.aether.core.graphics

import space.nexel.aether.core.sys.Resource
import space.nexel.aether.core.types.Vec2I
import space.nexel.aether.core.sys.NativeResource

object Texture {
  case class Config() extends Resource.Config
}

trait Texture extends NativeResource[Texture, Texture.Config] {
  def size: Vec2I
}
