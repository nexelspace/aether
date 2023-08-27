package space.nexel.aether.core.graphics

import space.nexel.aether.core.sys.Resource
import space.nexel.aether.core.sys.NativeResource

object ShaderProgram {
  case class Config(
      vertexShader: ShaderObject,
      fragmentShader: ShaderObject
  ) extends Resource.Config

}

trait ShaderProgram extends NativeResource[ShaderProgram, ShaderProgram.Config] {}
