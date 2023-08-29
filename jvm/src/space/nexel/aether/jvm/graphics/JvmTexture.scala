package space.nexel.aether.jvm.graphics

import space.nexel.aether.core.graphics.Texture
import space.nexel.aether.core.platform.Resource
import space.nexel.aether.core.graphics.Texture.Config
import space.nexel.aether.core.graphics.Texture.TextureFactory
import space.nexel.aether.core.types.Vec2I

object JvmTexture {
  val factory = new Resource.Factory[Texture, Texture.Config] {
    given TextureFactory = this
    def apply(config: Config) = new JvmTexture(config.size.get)
  }
}

class JvmTexture(val size: Vec2I)(using factory: TextureFactory) extends Texture {
  def release() = {
    factory.released(this)
  }

}
