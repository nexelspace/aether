package space.nexel.aether.core.graphics

import space.nexel.aether.core.platform.Resource
import space.nexel.aether.core.types.Vec2I
import space.nexel.aether.core.platform.NativeResource

object Texture {
  type TextureFactory = Resource.Factory[Texture, Config] 
  case class Config(size: Option[Vec2I]) extends Resource.Config
}

trait Texture extends NativeResource[Texture, Texture.Config] {
  def size: Vec2I
}
