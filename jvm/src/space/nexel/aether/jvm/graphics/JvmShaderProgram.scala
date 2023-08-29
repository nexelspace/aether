package space.nexel.aether.jvm.graphics

import space.nexel.aether.core.graphics.ShaderProgram
import space.nexel.aether.core.platform.Resource
import space.nexel.aether.core.graphics.ShaderProgram.Config
import space.nexel.aether.core.graphics.ShaderProgram.ShaderProgramFactory

object JvmShaderProgram {
  val factory = new Resource.Factory[ShaderProgram, ShaderProgram.Config] {
    given ShaderProgramFactory = this
    def apply(config: Config) = new JvmShaderProgram()
  }

}

class JvmShaderProgram(using factory: ShaderProgramFactory) extends ShaderProgram {
  def release() = {
    factory.released(this)
  }

}
