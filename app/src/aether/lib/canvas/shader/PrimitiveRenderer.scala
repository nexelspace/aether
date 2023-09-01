package aether.lib.canvas.shader

import PrimitiveRenderer.*
import aether.core.graphics.ShaderObject
import aether.core.graphics.ShaderProgram
import aether.lib.util.MatUtil
import aether.core.graphics.Graphics

object PrimitiveRenderer {
  val header = """
#ifdef GL_ES
precision mediump float;
precision mediump int;
#endif
"""
  val vertex2D = header + """
uniform mat4    u_mvpMatrix;
attribute vec4  a_position;
attribute vec4  a_color;

varying vec4    v_color;

void main(void)
{
	gl_Position = u_mvpMatrix * a_position;
  v_color = a_color;
}
"""

  val fragment2D = header + """
varying vec4 v_color;

void main (void)
{
  gl_FragColor = v_color;
}
"""


}

case class PrimitiveRenderer(shader: Shader)(using g: Graphics) extends ShaderCanvas.Renderer {
  val vertShaderPrim = ShaderObject(ShaderObject.Type.Vertex, vertex2D)
  val fragShaderPrim = ShaderObject(ShaderObject.Type.Fragment, fragment2D)
  val programPrim = ShaderProgram(vertShaderPrim, fragShaderPrim)

  def begin() = {
    shader.buffer.vertex.buffer.clear()
    shader.buffer.texCoord.buffer.clear()
    shader.buffer.colors.buffer.clear()
  }

  def end() = if (shader.buffer.vertex.position > 0) {
    // Renderer.get.blend = Blend.NormalPremultiplied
    // Renderer.get.blend = if (blend != Blend.Normal) blend else if (statePremultiplied) Blend.NormalPremultiplied else Blend.Normal
    val mvp = MatUtil.ortho(0, g.size.x, if (g.isTargetDisplay) g.size.y else -g.size.y, 0)

    val buffer = shader.buffer

    programPrim.uniform("u_mvpMatrix").get.putMat4F(mvp)
    programPrim.attributeBuffer("a_position", buffer.vertex.buffer, buffer.vertex.numComponents)
    programPrim.attributeBuffer("a_color", buffer.colors.buffer, buffer.colors.numComponents)
    //          programPrim.attribute("a_color").put4f(1,1,1,1)
    //		program.attributeBuffer(ATTRIBUTE_COLOR, colors);
    //	program.attributeBuffer(ATTRIBUTE_NORMAL, normals);
    //		program.attributeBuffer(ATTRIBUTE_TEX_COORD, uvs);
    programPrim.draw(ShaderProgram.Mode.Triangles, 0, buffer.vertex.size)
    //Log(s"Draw primitices $count")
  }
}
