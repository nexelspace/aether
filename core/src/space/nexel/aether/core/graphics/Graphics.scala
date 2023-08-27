package space.nexel.aether.core.graphics

import space.nexel.aether.core.platform.Resource

trait Graphics {
  val shaderProgramFactory: Resource.Factory[ShaderProgram, ShaderProgram.Config]
  val shaderObjectFactory: Resource.Factory[ShaderObject, ShaderObject.Config]
  val textureFactory: Resource.Factory[Texture, Texture.Config]

  def clear(r: Float, g: Float, b: Float, a: Float): Unit

}
