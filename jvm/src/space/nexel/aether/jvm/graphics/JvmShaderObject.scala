package space.nexel.aether.jvm.graphics

import space.nexel.aether.core.graphics.ShaderObject
import space.nexel.aether.core.platform.Resource
import space.nexel.aether.core.graphics.ShaderObject.Config
import space.nexel.aether.core.graphics.ShaderObject.ShaderObjectFactory

object JvmShaderObject {
  val factory = new Resource.Factory[ShaderObject, ShaderObject.Config] {
    given ShaderObjectFactory = this
    def apply(config: Config) = new JvmShaderObject()
  }
}

class JvmShaderObject(using factory: ShaderObjectFactory) extends ShaderObject {
  def release() = {
    factory.released(this)
  }
}
