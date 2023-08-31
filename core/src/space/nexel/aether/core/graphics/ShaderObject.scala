package space.nexel.aether.core.graphics

import space.nexel.aether.core.platform.Resource
import space.nexel.aether.core.platform.NativeResource
import space.nexel.aether.core.platform.Platform

object ShaderObject {
  type ShaderObjectFactory = Resource.Factory[ShaderObject, ShaderObject.Config] 
  case class Config(typ: Type, source: Option[String] = None) extends Resource.Config

  enum Type():
    case Fragment extends Type
    case Vertex extends Type

  def apply(typ: Type, source: String)(using platform: Platform): ShaderObject = {
    platform.shaderObjectFactory.create(Config(typ, Some(source)))
  }

}

trait ShaderObject extends NativeResource[ShaderObject, ShaderObject.Config] {
  
}
