package aether.core.graphics

import aether.core.platform.Resource
import aether.core.platform.NativeResource

object ShaderObject {
  type ShaderObjectFactory = Resource.Factory[ShaderObject, ShaderObject.Config] 
  case class Config(typ: Type, source: Option[String] = None) extends Resource.Config

  enum Type():
    case Fragment extends Type
    case Vertex extends Type

  def apply(typ: Type, source: String)(using graphics: Graphics): ShaderObject = {
    graphics.shaderObjectFactory.create(Config(typ, Some(source)))
  }

}

trait ShaderObject extends NativeResource[ShaderObject, ShaderObject.Config] {
  
}
