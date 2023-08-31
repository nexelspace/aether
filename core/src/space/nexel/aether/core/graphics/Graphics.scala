package space.nexel.aether.core.graphics

import space.nexel.aether.core.platform.Resource
import space.nexel.aether.core.types.Vec2I
import space.nexel.aether.core.types.RectI
import Graphics.*

object Graphics {
  trait RenderTarget {
    def size: Vec2I
  }

  
  implicit def flagToInt(flags: DepthTest): Int = flags.flags
  implicit def intToFlag(flags: Int): DepthTest = new DepthTest(flags)

  class DepthTest(val flags: Int) extends AnyVal
  object DepthTest {
    val Never = new DepthTest(0)
    val Less = new DepthTest(1)
    val Equal = new DepthTest(2)
    val LessEqual = new DepthTest(3)
    val Greater = new DepthTest(4)
    val NotEqual = new DepthTest(5)
    val GreaterEqual = new DepthTest(6)
    val Always = new DepthTest(7)
    val Write = new DepthTest(1 << 3)
    val Range0to1 = new DepthTest(1 << 4)
    val Range1to0 = new DepthTest(1 << 5)
  }

  enum Filter {
    case Undefined, Nearest, Linear
  }

  enum Blend {
    case None, Normal, NormalPremultiplied, Additive
  }

  enum Cull {
    case None, CW
  }

  class State(
      var viewport: RectI = null,
      var clip: RectI = null,
      var depthTest: DepthTest = DepthTest.Write | DepthTest.Always,
      var filter: Filter = Filter.Undefined,
      var blend: Blend = Blend.Normal,
      var cull: Cull = Cull.None
  ) {}
}

trait Graphics {


  protected val state: Graphics.State = new Graphics.State()

  def viewport: RectI = state.viewport
  def clip: RectI = state.clip
  def blend: Blend = state.blend
  def cull: Cull = state.cull
  def depthTest: DepthTest = state.depthTest
  def filter: Filter = state.filter

  def viewport_=(viewport: RectI): Unit
  def clip_=(clip: RectI): Unit
  def blend_=(blend: Blend): Unit
  def cull_=(cull: Cull): Unit
  def depthTest_=(depthTest: DepthTest): Unit
  def filter_=(filter: Filter): Unit

  def target: RenderTarget
  def isTargetDisplay = target.isInstanceOf[Display]
  def setTargetDisplay(display: Display): Unit
  def setTargetTexture(texture: Texture, area: RectI = null): Unit
  def size = target.size

  private[aether] def render(disp: Display, callback: (Display) => Unit): Unit

  def clear(r: Float, g: Float, b: Float, a: Float): Unit

}
