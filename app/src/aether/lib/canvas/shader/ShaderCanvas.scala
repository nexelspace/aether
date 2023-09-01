package aether.lib.canvas.shader

import aether.core.types.RectF
import aether.core.types.Vec2F
import aether.core.types.Vec2I
import aether.lib.canvas.Canvas
import aether.lib.font.Font

import ShaderCanvas._
import aether.core.graphics.Graphics
import aether.core.graphics.Display
import aether.lib.types.Tx2FAxis
import aether.core.graphics.Texture

object ShaderCanvas {

  val color = 0xffffffff

  trait Renderer {
    def begin(): Unit
    def end(): Unit
  }

  def apply(viewport: Vec2I)(using g: Graphics): ShaderCanvas = {
    new ShaderCanvas(RectF(Vec2F.Zero, viewport.toVec2F))
  }
 
}

class ShaderCanvas(var view: RectF, val tx: Tx2FAxis = Tx2FAxis.Identity)(using g: Graphics) extends Canvas {

  val buffer = new RenderBuffer(1024)

  val primitiveShader = new Shader(buffer, PrimitiveRenderer.vertex2D, PrimitiveRenderer.fragment2D)
  val textureShader = new Shader(buffer, TextureRenderer.vertex2D, TextureRenderer.fragment2D)

  var renderer: Option[Renderer] = None

  def copy(view: RectF, tx: Tx2FAxis): Canvas = {
    // Log(s"ShaderCanvas($view, $tx)")
    new ShaderCanvas(view, tx)
  }

  def useRenderer(next: Renderer) = {
    renderer match {
      case None                         => next.begin()
      case Some(last) if (last == next) => // keep last
      case Some(last) =>
        last.end()
        next.begin()
    }
    renderer = Some(next)
  }

  def flush() = {
    renderer.foreach(_.end())
    renderer = None
  }

  def clear(argb: Int): Unit = {
    useRenderer(PrimitiveRenderer(primitiveShader))
    fillRect(view, argb)
  }

  // -- rendering methods

  def drawTexture(area: RectF, texture: Texture): Unit = {
    useRenderer(new TextureRenderer(textureShader, texture))
    buffer.putTexture(tx.transformArea(area), RectF.unit)
  }

  def drawString(pos: Vec2F, font: Font, string: String): Unit = {
    useRenderer(new TextureRenderer(textureShader, font.texture))
    var cx = 0
    for (c <- string) {
      if (font.hasChar(c)) {
        val area = font.logicalArea(c)
        val target = RectF(pos.x + cx, pos.y, area.sizeX, area.sizeY)
        val source = RectF(area.x, area.y, target.sizeX, target.sizeY) * (Vec2F.One / font.texture.size.toVec2F)
        buffer.putTexture(tx.transformArea(target), source)
        cx += area.sizeX
      }
    }
  }

  def fillRect(area: RectF, color: Int): Unit = {
    useRenderer(new PrimitiveRenderer(primitiveShader))
    buffer.putVertices(buffer.vertex, tx.transformArea(area))
    buffer.putColor(color, 6)
  }

}
