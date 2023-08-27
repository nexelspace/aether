package space.nexel.aether.js.graphics

import org.scalajs.dom.raw.{WebGLRenderingContext => GL}
import space.nexel.aether.core.graphics.*

class JsGraphics(using gl: GL) extends Graphics {
  val shaderProgramFactory = JsShaderProgram.factory
  val shaderObjectFactory = JsShaderObject.factory
  val textureFactory = JsTexture.factory

  def clear(r: Float, g: Float, b: Float, a: Float): Unit = {
    gl.clearColor(r, g, b, a)
    gl.clear(GL.COLOR_BUFFER_BIT)
  }
}
