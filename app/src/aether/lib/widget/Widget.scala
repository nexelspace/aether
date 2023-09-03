package aether.lib.widget

import aether.core.platform.Event
import aether.lib.visual.Visual
import aether.core.types.RectF
import aether.core.types.Vec2F
import Widget.*
import aether.core.input.TouchEvent
import aether.lib.canvas.Canvas
import aether.core.platform.Module

object Widget {
  case class PickResult(visual: Visual, transform: Vec2F)

  // case class SetViewport(view: RectF) extends Event

  trait WidgetEvent extends Event
  case class PressEvent(pos: Vec2F) extends WidgetEvent
  case class ReleaseEvent(pos: Vec2F, clicked: Boolean) extends WidgetEvent
  case class DragEvent(pos: Vec2F) extends WidgetEvent

  object PinchEvent {
    def avg(pos: Seq[Vec2F]) = (pos(0) + pos(1)) / 2
  }
  case class PinchEvent(state: TouchEvent.Type, pos: Seq[Vec2F]) extends Event {
    assert(pos.size==2)
  }
  // case class ScrollEvent(offset: Vec2F) extends Event
  // case class ZoomEvent(zoom: Float) extends Event
  
}

trait Widget extends Visual with Module {

  /** Pich child Visuals. */
  def pick(transform: Vec2F, pos: Vec2F): Seq[PickResult] = Seq()

  /** Resize widget to minimum size. */
  def resizeMinimum(): Unit = {}

  /**
   * Resize widget to given size.
   */
  def resize(containerSize: Vec2F): Unit ={}
  

}
