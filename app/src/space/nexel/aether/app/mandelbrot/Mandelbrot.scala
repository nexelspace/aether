package space.nexel.aether.app.mandelbrot

import Mandelbrot.Config
import space.nexel.aether.core.types.Vec2I
import space.nexel.aether.core.types.Vec2F
import space.nexel.aether.core.platform.Event
import space.nexel.aether.core.platform.Module
import space.nexel.aether.core.graphics.Display
import space.nexel.aether.core.input.PointerEvent.MouseButton
import space.nexel.aether.core.input.PointerEvent.MouseMove
import space.nexel.aether.core.input.PointerEvent.MouseWheel
import space.nexel.aether.core.input.KeyEvent
import space.nexel.aether.core.graphics.ShaderProgram
import space.nexel.aether.core.graphics.ShaderBuffer.*
import space.nexel.aether.core.graphics.Texture
import space.nexel.aether.core.platform.Platform
import space.nexel.aether.core.platform.Log
import space.nexel.aether.lib.canvas.shader.ShaderVarBuffer
import space.nexel.aether.core.graphics.ShaderProgram
import space.nexel.aether.core.util.Colors
import space.nexel.aether.core.graphics.Graphics
import space.nexel.aether.lib.canvas.shader.ShaderCanvas
import space.nexel.aether.core.types.RectF
import space.nexel.aether.core.platform.Resource
import space.nexel.aether.core.platform.Dispatcher
import space.nexel.aether.core.input.KeyEvent.Code.L

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
  val display = Display(Config.dispSize)
  given g: Graphics = display.graphics

  /** Select painter code. */
  val painter = if (true) new ShaderPainter else new TexturePainter

  val iterations = 400
  var pointerStart = Vec2F.Zero
  var mousePos = Vec2F.Zero
  var scale = 4.0f
  var center = Vec2F(-0.01f, -0.01f)

  lazy val canvas = ShaderCanvas(display.size)

  override def event(event: Event) = {
    event match {
      case Module.Init(_) =>
        painter.init()
      case Module.Uninit    =>
      case u: Module.Update =>
      case Display.Paint(disp) =>
        painter.paint()
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
    center = center + (t / Config.size * 2) * scale
  }

  def screenToUnit(screen: Vec2F) = screen / Config.size * 2 - 1

  abstract class Painter {
    def init(): Unit
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

    override def paint() = {
      program.get.foreach { program =>
        program.attributeBuffer("a_position", vertexBuffer.buffer, vertexBuffer.numComponents)
        program.uniform("iter").get.putI(iterations)
        program.uniform("scale").get.putF(scale)
        program.uniform("center").get.put2F(center)
        program.draw(ShaderProgram.Mode.Triangles, 0, 6)
      }
    }
  }

  class TexturePainter extends Painter {
    val dispTex = Texture(Texture.Flag.Writable, Config.dispSize.x, Config.dispSize.y)

    override def init() = {}

    override def paint() = {
      updateTexture()
      canvas.drawTexture(RectF(Vec2F.Zero, Config.dispSize.toVec2F), dispTex)
      canvas.flush()
    }

    def updateTexture() = {
      for {
        y <- 0 until Config.size
        x <- 0 until Config.size
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
