package space.nexel.aether.jvm.graphics

import space.nexel.aether.core.graphics.*

class JvmGraphics extends Graphics {
  val shaderProgramFactory = JvmShaderProgram.factory
  val shaderObjectFactory = JvmShaderObject.factory
  val textureFactory = JvmTexture.factory

  def clear(r: Float, g: Float, b: Float, a: Float): Unit = {
    import org.lwjgl.opengl.GL11._
    glClearColor(r, g, b, a)
    glClear(GL_COLOR_BUFFER_BIT)
  }
  
}
