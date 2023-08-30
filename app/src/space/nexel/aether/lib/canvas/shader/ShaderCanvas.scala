package space.nexel.aether.lib.canvas.vertex

import aether.core.Log
import aether.core.platform.shader.ShaderObject
import aether.core.platform.shader.ShaderProgram
import aether.core.platform.shader.Texture
import aether.core.types.RectF
import space.nexel.aether.core.types.Vec2F
import space.nexel.aether.core.types.Vec2I
import aether.lib.canvas.Canvas
import aether.lib.canvas_v1.MatUtil
import aether.lib.graphics.Font
import aether.lib.graphics.Visual
import aether.lib.shaders.ShaderVarBuffer
import aether.core.platform.shader

import ShaderCanvas._
import aether.core.types.Tx2FAxis
import aether.core.platform.shader.Display

object ShaderCanvas {

  val buffer = new RenderBuffer(1024)

  val color = 0xffffffff

  trait Renderer {
    def begin(): Unit
    def end(): Unit
  }

  def apply(): ShaderCanvas = {
    new ShaderCanvas(RectF(Vec2F.Zero, Display.primary.size.toVec2F))
  }
 
}

class ShaderCanvas(val view: RectF, val tx: Tx2FAxis = Tx2FAxis.Identity) extends Canvas {


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
    useRenderer(new PrimitiveRenderer())
    fillRect(view, argb)
  }

  // -- rendering methods

  def drawTexture(area: RectF, texture: Texture): Unit = {
    useRenderer(new TextureRenderer(texture))
    buffer.putTexture(tx.transformArea(area), RectF.unit)
  }

  def drawString(pos: Vec2F, font: Font, string: String): Unit = {
    useRenderer(new TextureRenderer(font.texture))
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
    useRenderer(new PrimitiveRenderer())
    buffer.putVertices(buffer.vertex, tx.transformArea(area))
    buffer.putColor(color, 6)
  }

}
