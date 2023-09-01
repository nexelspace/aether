package aether.app.mandelbrot

import Mandelbrot.Config
import aether.core.types.Vec2I
import aether.core.types.Vec2F
import aether.core.platform.Event
import aether.core.platform.Module
import aether.core.graphics.Display
import aether.core.input.PointerEvent.MouseButton
import aether.core.input.PointerEvent.MouseMove
import aether.core.input.PointerEvent.MouseWheel
import aether.core.input.KeyEvent
import aether.core.graphics.ShaderProgram
import aether.core.graphics.ShaderBuffer.*
import aether.core.graphics.Texture
import aether.core.platform.Platform
import aether.core.platform.Log
import aether.lib.canvas.shader.ShaderVarBuffer
import aether.core.graphics.ShaderProgram
import aether.core.util.Colors
import aether.core.graphics.Graphics
import aether.lib.canvas.shader.ShaderCanvas
import aether.core.types.RectF
import aether.core.platform.Resource
import aether.core.platform.Dispatcher
import aether.core.input.KeyEvent.Code.L

object Mandelbrot {

  object Config {
    val depth: Int = 9
    val size = 1 << depth
    val dispSize = Vec2I(size, size)
  }
}

class Mandelbrot(val platform: Platform) extends Module {
  given Platform = platform
  given Dispatcher = platform.dispatcher
  val display = Display(Config.dispSize, platform.name == Platform.Name.Js)
  def size = display.size
  given g: Graphics = display.graphics

  /** Select painter code. */
  val painter = if (true) new ShaderPainter else new TexturePainter

  val iterations = 400
  var pointerStart = Vec2F.Zero
  var mousePos = Vec2F.Zero
  var scale = 4.0f
  var center = Vec2F(-0.01f, -0.01f)

  lazy val canvas = ShaderCanvas(size)

  override def event(event: Event) = {
    event match {
      case Module.Init(_) =>
        painter.init()
      case Module.Uninit    =>
      case u: Module.Update =>
      case Display.Paint(disp) =>
        painter.paint()
      case Display.Resize(disp, size: Vec2I) =>
        painter.resize(size)
      case MouseButton(true, pos, _) =>
        pointerStart = pointerUpdate(pos)
      case MouseMove(true, pos, _) =>
        pointerUpdate(pos)
        val offset = pointerStart - pos
        translateScreen(offset)
        pointerStart = pos
      case MouseWheel(pos, wheel) =>
        // translateScreen(pos)
        Log(s"Translate $pos")
        scale = scale * Math.exp(-0.4 * wheel).toFloat
      // translateScreen(-pos)
      case KeyEvent(true, true, keyCode, _) =>
        keyCode match {
          case KeyEvent.Code.SPACE =>
          case _                   =>
        }
      case w => // Log(s"Event $event")
    }
  }

  def pointerUpdate(pos: Vec2F): Vec2F = {
    mousePos = pos
    pos
  }

  def translateScreen(t: Vec2F) = {
    center = center + (t / size.y * 2) * scale
  }

  def screenToUnit(screen: Vec2F) = screen / size.y * 2 - 1

  abstract class Painter {
    def init(): Unit
    def resize(size: Vec2I): Unit
    def paint(): Unit
  }

  class ShaderPainter extends Painter {
    val resources = platform.resource(Mandelbrot.this)
    val program = Resource.sequence {
      Seq("mandelbrot.vs", "mandelbrot.fs").map(resources.loadString)
    } map { case Seq(vs, fs) =>
      Log(s"Shader loaded")
      ShaderProgram(vs, fs)
    }

    // val program = ShaderProgram(Shaders.vertex, Shaders.fragment)
    val vertexBuffer = ShaderVarBuffer(Target.Vertex | Type.Float, 6, 2)

    override def init() = {
      vertexBuffer.put2F(-1, -1)
      vertexBuffer.put2F(-1, +1)
      vertexBuffer.put2F(+1, -1)
      vertexBuffer.put2F(+1, -1)
      vertexBuffer.put2F(-1, +1)
      vertexBuffer.put2F(+1, +1)
    }

    def resize(size: Vec2I)= {}

    override def paint() = {
      program.get.foreach { program =>
        program.attributeBuffer("a_position", vertexBuffer.buffer, vertexBuffer.numComponents)
        program.uniform("iter").get.putI(iterations)
        program.uniform("scale").get.put2F(scale / size.y * size.x, scale)
        program.uniform("center").get.put2F(center)
        program.draw(ShaderProgram.Mode.Triangles, 0, 6)
      }
    }
  }

  class TexturePainter extends Painter {
    var dispTex = Texture(Texture.Flag.Writable, size.x, size.y)

    override def init() = {}

    def resize(size: Vec2I)= {
      dispTex.release()
      Log(s"Resize texture to $size")
      dispTex = Texture(Texture.Flag.Writable, size.x, size.y)
    }

    override def paint() = {
      updateTexture()
      canvas.drawTexture(RectF(Vec2F.Zero, size.toVec2F), dispTex)
      canvas.flush()
    }

    def updateTexture(): Unit = {
      assert(dispTex.size == size, s"Texture size ${dispTex.size}, display: $size")
      for {
        y <- 0 until size.y
        x <- 0 until size.x
      } {
        val s = screenToUnit(Vec2F(x, y)) * scale + center
        val cr = s.x
        val ci = s.y
        var iteration = 0
        var max = 100
        var r0 = 0.0
        var i0 = 0.0
        while (r0 * r0 + i0 * i0 <= 4 && iteration < max) {
          val r1 = r0 * r0 - i0 * i0 + cr
          val i1 = 2 * r0 * i0 + ci
          r0 = r1
          i0 = i1
          iteration += 1
        }
        val color = Colors.hsl(2f * iteration / max, 1, if (iteration == max) 0 else 0.5f)
        dispTex.setARGB(color, x, y)
      }
    }
  }
}
