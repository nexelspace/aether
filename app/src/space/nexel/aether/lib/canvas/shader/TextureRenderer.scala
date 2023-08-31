package space.nexel.aether.lib.canvas.shader

import space.nexel.aether.core.graphics.Texture
import TextureRenderer.*
import space.nexel.aether.core.graphics.ShaderObject
import space.nexel.aether.core.graphics.ShaderProgram
import space.nexel.aether.core.graphics.Graphics
import space.nexel.aether.lib.util.MatUtil

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



}

case class TextureRenderer(shader: Shader, texture: Texture)(using g: Graphics) extends ShaderCanvas.Renderer {

  def begin() = {
    shader.buffer.vertex.clear()
    shader.buffer.texCoord.clear()
  }

  def end() = {
    // Renderer.get.blend = if (blend != Blend.Normal) blend else if (statePremultiplied) Blend.NormalPremultiplied else Blend.Normal
    val mvp = MatUtil.ortho(0, g.size.x, if (g.isTargetDisplay) g.size.y else -g.size.y, 0)

    shader.programTex.textureUnit(0, texture)
    shader.programTex.uniform("u_mvpMatrix").get.putMat4F(mvp)
    shader.programTex.attributeBuffer("a_position", shader.buffer.vertex.buffer, shader.buffer.vertex.numComponents)
    // programTex.attributeBuffer("a_color", colorBuffer.buffer, colorBuffer.numComponents)
    shader.programTex.attribute("a_color").get.put4F(1, 1, 1, 1)
    shader.programTex.attributeBuffer("a_texCoord", shader.buffer.texCoord.buffer, shader.buffer.texCoord.numComponents)
    shader.programTex.draw(ShaderProgram.Mode.Triangles, 0, shader.buffer.vertex.size)
  }
}
