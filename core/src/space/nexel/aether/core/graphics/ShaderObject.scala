package space.nexel.aether.core.graphics

import space.nexel.aether.core.platform.Resource
import space.nexel.aether.core.platform.NativeResource

object ShaderObject {
  type ShaderObjectFactory = Resource.Factory[ShaderObject, ShaderObject.Config] 
  case class Config(typ: Type) extends Resource.Config

  enum Type():
    case Fragment extends Type
    case Vertex extends Type
}

trait ShaderObject extends NativeResource[ShaderObject, ShaderObject.Config] {
  
}
