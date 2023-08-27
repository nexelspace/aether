package space.nexel.aether.core.graphics

import space.nexel.aether.core.platform.Resource
import space.nexel.aether.core.platform.NativeResource

object ShaderProgram {
  type ShaderProgramFactory = Resource.Factory[ShaderProgram, Config]
  case class Config(
      vertexShader: ShaderObject,
      fragmentShader: ShaderObject
  ) extends Resource.Config

}

trait ShaderProgram extends NativeResource[ShaderProgram, ShaderProgram.Config] {}
