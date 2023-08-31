package space.nexel.aether.lib.canvas.shader

import space.nexel.aether.core.graphics.ShaderProgram
import space.nexel.aether.core.graphics.ShaderObject
import space.nexel.aether.core.graphics.Graphics
import space.nexel.aether.core.platform.Platform

class Shader(val buffer: RenderBuffer, vertexSrc: String, fragmentSrc: String)(using platform: Platform) {

  val vertShaderTex = ShaderObject(ShaderObject.Type.Vertex, vertexSrc)
  val fragShaderTex = ShaderObject(ShaderObject.Type.Fragment, fragmentSrc)
  val programTex = ShaderProgram(vertShaderTex, fragShaderTex)

}
