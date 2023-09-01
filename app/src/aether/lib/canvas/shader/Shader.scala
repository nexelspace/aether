package aether.lib.canvas.shader

import aether.core.graphics.ShaderProgram
import aether.core.graphics.ShaderObject
import aether.core.graphics.Graphics

class Shader(val buffer: RenderBuffer, vertexSrc: String, fragmentSrc: String)(using g: Graphics) {

  val vertShaderTex = ShaderObject(ShaderObject.Type.Vertex, vertexSrc)
  val fragShaderTex = ShaderObject(ShaderObject.Type.Fragment, fragmentSrc)
  val programTex = ShaderProgram(vertShaderTex, fragShaderTex)

}
