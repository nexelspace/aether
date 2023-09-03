package aether.app.canvas

import aether.core.types.Vec2F
import aether.core.platform.Event
import aether.core.input.PointerEvent.MouseButton
import aether.core.input.PointerEvent.MouseMove
import aether.lib.widget.Widget.PressEvent
import aether.lib.widget.Widget.ReleaseEvent
import aether.lib.widget.Widget.DragEvent
import aether.core.platform.Log

trait MouseDraw {
  private var startPos: Option[Vec2F] = None

  def drawEvent(event: Event): Boolean = {
    event match {
      // case MouseButton(true, pos, MouseButton.Left) =>
      case PressEvent(pos) =>
        // Log(s"Draw at $pos")
        startPos = Some(pos)
        drawBegin(pos)
        true
      // case MouseButton(false, pos, MouseButton.Left) =>
      case ReleaseEvent(pos, _) =>
        // Log(s"Release at $pos")
        startPos = None
        drawEnd(pos)
        true
      // case MouseMove(true, pos, _) if (startPos.isDefined) =>
      case DragEvent(pos) =>
        draw(pos)
        true
      case _ =>
        false
    }
  }

  def drawBegin(pos: Vec2F): Unit = {
    draw(pos)
  }

  def drawEnd(pos: Vec2F): Unit = {
  }

  def draw(pos: Vec2F): Unit

}
