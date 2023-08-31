package space.nexel.aether.lib.canvas.shader

import space.nexel.aether.core.graphics.ShaderProgram
import space.nexel.aether.core.graphics.ShaderObject
import space.nexel.aether.core.graphics.Graphics

class Shader(val buffer: RenderBuffer, vertexSrc: String, fragmentSrc: String)(using g: Graphics) {

  val vertShaderTex = ShaderObject.create(ShaderObject.Type.Vertex, vertexSrc)
  val fragShaderTex = ShaderObject.create(ShaderObject.Type.Fragment, fragmentSrc)
  val programTex = ShaderProgram.create(vertShaderTex, fragShaderTex)

}
