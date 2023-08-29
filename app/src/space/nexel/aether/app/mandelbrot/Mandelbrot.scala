package aether.app.fractal

import Mandelbrot.Config
import space.nexel.aether.core.types.Vec2I
import space.nexel.aether.core.types.Vec2F
import space.nexel.aether.core.platform.Event
import space.nexel.aether.core.platform.Module
import space.nexel.aether.core.platform.Display
import space.nexel.aether.core.input.PointerEvent.MouseButton
import space.nexel.aether.core.input.PointerEvent.MouseMove
import space.nexel.aether.core.input.PointerEvent.MouseWheel
import space.nexel.aether.core.input.KeyEvent
import space.nexel.aether.core.graphics.ShaderProgram
import space.nexel.aether.core.graphics.Texture
import space.nexel.aether.core.platform.Platform
import space.nexel.aether.core.platform.Log

object Mandelbrot {

  object Config {
    val depth: Int = 9
    val size = 1 << depth
    val dispSize = Vec2I(size, size)
  }
}

class Mandelbrot(val platform: Platform) extends Module {

  val display = platform.displayFactory(Display.Config(size = Config.dispSize))

  /** Select painter code. */
  lazy val painter = if (true) new ShaderPainter else new TexturePainter

  val iterations = 400
  var pointerStart = Vec2F.Zero
  var mousePos = Vec2F.Zero
  var scale = 4.0f
  var center = Vec2F(-0.01f, -0.01f)

  override def event(event: Event) = {
    event match {
      case Module.Init() =>
        painter.init()
      case Module.Uninit =>
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
        translateScreen(pos)
        Log(s"Translate $pos")
        scale = scale * Math.exp(-0.4 * wheel).toFloat
        translateScreen(-pos)
      case KeyEvent(true, true, keyCode, _) =>
        keyCode match {
          case KeyEvent.Code.SPACE =>
          case _              =>
        }
      case w => //Log(s"Event $event")
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
    val program = display.graphics.shaderProgramFactory(Shaders.vertex, Shaders.fragment)
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
      program.attributeBuffer("a_position", vertexBuffer.buffer, vertexBuffer.numComponents)
      program.uniform("iter").get.putI(iterations)
      program.uniform("scale").get.putF(scale)
      program.uniform("center").get.put2F(center)
      Log.once("paint")
      program.draw(ShaderProgram.Mode.Triangles, 0, 6)
    }
  }

  class TexturePainter extends Painter {
    val dispTex = Texture(Config.dispSize.x, Config.dispSize.y)

    override def init() = {}

    override def paint() = {
      updateTexture()
      Renderer.get.setTargetDisplay()
      val canvas = VertexCanvas()
      // canvas.begin()
      // canvas.clear(0)
      // canvas.transform { t =>
      //   t.scale(Config.dispSize.x, Config.dispSize.y)
      // }
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
