package aether.lib.widget

import aether.core.input.PointerEvent.*
import aether.core.input.TouchEvent.Type.*
import aether.core.math.MathF
import aether.core.types.Mat3F
import aether.core.types.RectF
import aether.core.types.Vec2F
import aether.lib.widget.Widget.*
import Scroll._
import aether.core.platform.Event
import aether.lib.types.Tx2FAxis

object Scroll {
  case class Zoom(minExp: Int = -1, maxExp: Int = 5, fractions: Float = 1) {
    def scalar(exp: Float) = Math.pow(2, exp / fractions).toFloat
    def min = scalar(minExp)
    def max = scalar(maxExp)
    def isDefined = minExp != maxExp
  }
  case class Config(
      zoom: Zoom = Zoom(),
      //align:  Vec2F               = Vec2F(0, 0),
      bounds: Option[RectF] = None,
      boundsReactive: Option[() => RectF] = None,
      mouseButton: Int = MouseButton.Right
  )

  def create(zoom: Zoom = Zoom())(bounds: => RectF): Scroll = {
    new Scroll(Config(zoom, None, Some(() => bounds)))
  }
}

class Scroll(config: Config) extends aether.core.platform.Module {

  var pos: Vec2F = Vec2F.Zero

  var startPos: Option[Vec2F] = None

  var scale = 1f

  var pinchStart: Option[Seq[Vec2F]] = None

  def event(event: Event) = {
    event match {
      case MouseButton(true, p, config.mouseButton) =>
        startPos = Some(p - pos)
      case MouseButton(false, p, config.mouseButton) if (startPos.isDefined) =>
        pos = clampPos(p - startPos.get)
        startPos = None
      case MouseMove(_, p, _) if (startPos.isDefined) =>
        pos = clampPos(p - startPos.get)
      case MouseWheel(p, wheel) =>
        zoom(config.zoom.scalar(wheel.toInt), p)
      case PinchEvent(Start, pinchPos) =>
        // Log(s"Scroll start $pinchStart, $pinchPos")
        pinchStart = Some(pinchPos)
      case PinchEvent(Move, pinchPos) =>
        // Log(s"Scroll move $pinchStart, $pinchPos")
        val move = PinchEvent.avg(pinchPos) - PinchEvent.avg(pinchStart.get)
        def vector(p: Seq[Vec2F]): Vec2F = p(1)-p(0)
        val s = vector(pinchPos).norm / vector(pinchStart.get).norm
        zoom(s, PinchEvent.avg(pinchPos))
        pos += move
        pinchStart = Some(pinchPos)
      case PinchEvent(End, pinchPos) =>
        pinchStart = None
      case _ =>
    }
  }

  def clampPos(p: Vec2F): Vec2F = {
    if (config.bounds.isDefined) {
      ???
    } else p
  }

  def zoom(scaling: Float, center: Vec2F) = {
    val oldScale = scale
    scale *= scaling
    scale = MathF.clamp(scale, config.zoom.min, config.zoom.max)
    pos += (pos - center) * (scale / oldScale - 1)
    pos = clampPos(pos)
  }

  def tx: Tx2FAxis = Tx2FAxis(Vec2F(scale), pos)
  def txView: Tx2FAxis = tx
  def txModel: Tx2FAxis = tx.inv
  // def txMat3: Mat3F = tx.toMat3F

}
