package aether.lib.canvas.shader

import aether.core.graphics.ShaderBuffer.Flag
import aether.core.graphics.ShaderBuffer.Size
import aether.core.graphics.ShaderBuffer.Target
import aether.core.graphics.ShaderBuffer.Type
import aether.core.types.RectF
import aether.core.graphics.Graphics
import aether.core.types.Color

class RenderBuffer(size: Int)(using g: Graphics) {
  val vertex = ShaderVarBuffer(Size.Dynamic | Target.Vertex | Type.Float, size, 3)
  val colors = ShaderVarBuffer(Size.Dynamic | Target.Vertex | Type.UByte | Flag.Normalize, size, 4)
  val texCoord = ShaderVarBuffer(Size.Dynamic | Target.Vertex | Type.Float, size, 2)

  inline def putVertices(buffer: ShaderVarBuffer, rect: RectF): Unit = {
    buffer.put2F(rect.x, rect.y)
    buffer.put2F(rect.x, rect.y2)
    buffer.put2F(rect.x2, rect.y)
    buffer.put2F(rect.x2, rect.y2)
    buffer.put2F(rect.x2, rect.y)
    buffer.put2F(rect.x, rect.y2)
  }

  def putTexture(x: Float, y: Float): Unit = {
    putTexture(RectF(x, y, 1, 1))
  }

  def putTexture(target: RectF, source: RectF = RectF.unit): Unit = {
    putVertices(vertex, target)
    putVertices(texCoord, source)
    // colorBuffer.clear()
    // putColor(color, 6)
  }

  val statePremultiplied = false
  def putColor(color: Int, count: Int) = {
    val c = Color(color)
    val a = c.a
    val (r, g, b) = if (statePremultiplied) (c.RxA, c.GxA, c.BxA) else (c.r, c.g, c.b)
    for (i <- 0 until count) {
      colors.put4I(r, g, b, a);
    }
  }

  override def toString = Seq(vertex, colors, texCoord).map(_.toString).mkString("[", " ", "]")
}
