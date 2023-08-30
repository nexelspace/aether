package space.nexel.aether.core.graphics

import space.nexel.aether.core.platform.Resource
import space.nexel.aether.core.types.Vec2I
import Graphics.RenderTarget
import space.nexel.aether.core.platform.Display
import space.nexel.aether.core.types.RectI

object Graphics {
  trait RenderTarget {
    def size: Vec2I
  }
}

trait Graphics {
  val shaderProgramFactory: Resource.Factory[ShaderProgram, ShaderProgram.Config]
  val shaderObjectFactory: Resource.Factory[ShaderObject, ShaderObject.Config]
  val shaderBufferFactory: Resource.Factory[ShaderBuffer, ShaderBuffer.Config]
  val textureFactory: Resource.Factory[Texture, Texture.Config]

  def target: RenderTarget
  def isTargetDisplay = target.isInstanceOf[Display]
  def setTargetDisplay(): Unit
  def setTargetTexture(texture: Texture, area: RectI = null): Unit

  def clear(r: Float, g: Float, b: Float, a: Float): Unit

}
