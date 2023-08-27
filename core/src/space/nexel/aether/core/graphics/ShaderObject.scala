package space.nexel.aether.core.graphics

import space.nexel.aether.core.sys.Resource
import space.nexel.aether.core.sys.NativeResource

object ShaderObject {

  case class Config(typ: Type) extends Resource.Config

  enum Type():
    case Fragment extends Type
    case Vertex extends Type
}

trait ShaderObject extends NativeResource[ShaderObject, ShaderObject.Config] {
  
}
