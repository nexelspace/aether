package space.nexel.aether.lib.canvas.vertex

import aether.core.platform.shader.Texture
import TextureRenderer.*
import aether.core.platform.shader.ShaderObject
import aether.core.platform.shader.ShaderProgram
import aether.core.platform.shader.Renderer
import aether.lib.canvas_v1.MatUtil
import aether.core.meta.Dev

object TextureRenderer {
  val header = """
#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif
"""

  val vertex2D = header + """
uniform mat4    u_mvpMatrix;
attribute vec4 a_position;
attribute vec4 a_color;
attribute vec4 a_texCoord;

varying vec4 v_texCoord;
varying vec4 v_color;

void main()
{
	gl_Position = u_mvpMatrix * a_position;
	v_texCoord = a_texCoord;
	v_color = a_color;
}
"""

  val fragment2D = header + """
uniform sampler2D u_texture0;

varying vec4 v_color;
varying vec4 v_texCoord;

void main()
{
	vec4 texColor = texture2D(u_texture0, v_texCoord.xy);
	if (texColor.a == 0.0) discard;
  gl_FragColor = v_color * texColor;
}
"""


  val vertShaderTex = ShaderObject.create(ShaderObject.Type.Vertex, vertex2D)
  val fragShaderTex = ShaderObject.create(ShaderObject.Type.Fragment, fragment2D)
  val programTex = ShaderProgram.create(vertShaderTex, fragShaderTex)

  val buffer = VertexCanvas.buffer

}

case class TextureRenderer(texture: Texture) extends VertexCanvas.Renderer {

  def begin() = {
    buffer.vertex.clear()
    buffer.texCoord.clear()
  }

  def end() = {
    val r = Renderer.get
    // Renderer.get.blend = if (blend != Blend.Normal) blend else if (statePremultiplied) Blend.NormalPremultiplied else Blend.Normal
    val mvp = MatUtil.ortho(0, r.sizeX, if (r.isTargetDisplay) r.sizeY else -r.sizeY, 0)

    programTex.textureUnit(0, texture)
    programTex.uniform("u_mvpMatrix").get.putMat4F(mvp)
    programTex.attributeBuffer("a_position", buffer.vertex.buffer, buffer.vertex.numComponents)
    // programTex.attributeBuffer("a_color", colorBuffer.buffer, colorBuffer.numComponents)
    programTex.attribute("a_color").get.put4F(1, 1, 1, 1)
    programTex.attributeBuffer("a_texCoord", buffer.texCoord.buffer, buffer.texCoord.numComponents)
    Dev.wrapThrowable(s"ShaderProgram.draw failed, buffer $buffer") {
      programTex.draw(ShaderProgram.Mode.Triangles, 0, buffer.vertex.size)
    }
  }
}
