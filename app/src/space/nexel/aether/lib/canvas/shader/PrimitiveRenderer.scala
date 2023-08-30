package space.nexel.aether.lib.canvas.vertex

import PrimitiveRenderer.*
import space.nexel.aether.core.graphics.ShaderObject
import space.nexel.aether.core.graphics.ShaderProgram
import space.nexel.aether.lib.util.MatUtil

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

  val vertShaderPrim = ShaderObject.create(ShaderObject.Type.Vertex, vertex2D)
  val fragShaderPrim = ShaderObject.create(ShaderObject.Type.Fragment, fragment2D)
  val programPrim = ShaderProgram.create(vertShaderPrim, fragShaderPrim)

  val buffer = VertexCanvas.buffer

}

class PrimitiveRenderer extends VertexCanvas.Renderer {

  def begin() = {
    buffer.vertex.buffer.clear()
    buffer.texCoord.buffer.clear()
    buffer.colors.buffer.clear()
  }

  def end() = if (buffer.vertex.position > 0) {
    // Renderer.get.blend = Blend.NormalPremultiplied
    // Renderer.get.blend = if (blend != Blend.Normal) blend else if (statePremultiplied) Blend.NormalPremultiplied else Blend.Normal
    val r = Renderer.get
    val mvp = MatUtil.ortho(0, r.sizeX, if (r.isTargetDisplay) r.sizeY else -r.sizeY, 0)

    programPrim.uniform("u_mvpMatrix").get.putMat4F(mvp)
    programPrim.attributeBuffer("a_position", buffer.vertex.buffer, buffer.vertex.numComponents)
    programPrim.attributeBuffer("a_color", buffer.colors.buffer, buffer.colors.numComponents)
    //          programPrim.attribute("a_color").put4f(1,1,1,1)
    //		program.attributeBuffer(ATTRIBUTE_COLOR, colors);
    //	program.attributeBuffer(ATTRIBUTE_NORMAL, normals);
    //		program.attributeBuffer(ATTRIBUTE_TEX_COORD, uvs);
    Dev.wrapThrowable(s"ShaderProgram.draw failed, buffer $buffer") {
      programPrim.draw(ShaderProgram.Mode.Triangles, 0, buffer.vertex.size)
    }
    //Log(s"Draw primitices $count")
  }
}
