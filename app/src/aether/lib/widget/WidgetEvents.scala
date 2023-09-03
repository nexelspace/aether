package aether.lib.widget


import scala.collection.immutable.SortedMap
import scala.collection.mutable.Buffer
import aether.lib.canvas.Canvas
import aether.core.types.RectF
import scala.collection.mutable
import aether.core.platform.Module
import aether.core.types.Vec2F
import aether.core.platform.Event
import aether.core.graphics.Display
import aether.core.platform.Log
import aether.core.input.PointerEvent.*
import aether.core.input.TouchEvent
import aether.core.input.TouchEvent.*
import aether.core.input.TouchEvent.Type.*
import Widget.*
import aether.lib.visual.Visual

object WidgetEvents {
}

/** config:
  *   pinch treshold
  */
class WidgetEvents(root: Widget) extends Module {

  enum State {
    case Inactive
    case Init(start: Vec2F)
    case Drag
    case Pinch
  }
  import State.*
  var state: State = Inactive
  val dragInitEvents = Buffer[Event]()

  var focus: Option[PickResult] = None

  val pinchTreshold = 10 // config.value[Float]("pinch/treshold", 10)

  var beginPinch: Seq[Vec2F] = _
  var touchOption: Option[List[Event]] = None

  def pickWidget(pos: Vec2F): Option[PickResult] =
    root.pick(Vec2F.Zero, pos).filter(_.visual.isInstanceOf[Widget]).headOption


  def event(event: Event): Unit = {
    val processed: Boolean = event match {
      case MouseButton(true, pos, MouseButton.Left) =>
        focus = pickWidget(pos)
        (focus: @unchecked) match {
          case Some(PickResult(widget: Widget, trans)) =>
            widget.event(event)
            widget.event(PressEvent(pos - trans))
            true
          case None => false
        }
      case MouseButton(false, pos, MouseButton.Left) if (focus.isDefined) =>
        val picked = pickWidget(pos)
        (focus: @unchecked) match {
          case Some(PickResult(widget: Widget, trans)) =>
            widget.event(ReleaseEvent(pos - trans, focus == picked))
        }
        focus = None
        true
      case MouseMove(true, pos, _) if (focus.isDefined) =>
        focus map {
          case PickResult(widget: Widget, trans) =>
            widget.event(event)
            widget.event(DragEvent(pos - trans))
        }
        true
      case TouchEvent(typ, id, pos, touchMap) =>
        handleTouch(typ, id, pos, touchMap)
        true
      case _ =>
        false
    }

    if (!processed) root.event(event)
  }

  def handleTouch(typ: TouchEvent.Type, id: Int, pos: Vec2F, touchMap: Map[Int, Vec2F]) = {
    var sortedTouches = (SortedMap[Int, Vec2F]() ++ touchMap)
    if (typ == End) {
      // add ended event pos to collection
      sortedTouches += (id -> pos)
    }
    val touches = sortedTouches.values.toSeq

    // Log(s"Event $typ $id $pos $touches")
    if (typ == Start && touches.size == 1) {
      focus = pickWidget(pos)
      // Log(s"Start focus $focus")
    }

    focus map {
      case PickResult(widget: Widget, trans) =>
        (typ, touches.size, state) match {
          case (Start, 1, _) =>
            state = Init(pos)
            dragInitEvents.clear()
            dragInitEvents += PressEvent(pos - trans)
          case (Start, 2, Init(_)) =>
            state = Pinch
            widget.event(PinchEvent(Start, touches))
          case (End, 1, Drag) =>
            widget.event(ReleaseEvent(pos - trans, focus == pickWidget(pos)))
            state = Inactive
            focus = None
          case (End, 1, Init(_)) =>
            dragInitEvents.foreach(widget.event)
            widget.event(ReleaseEvent(pos - trans, focus == pickWidget(pos)))
            state = Inactive
            focus = None
          case (End, 2, Pinch) =>
            widget.event(PinchEvent(End, touches))
            state = Inactive
            focus = None
          case (Move, 1, Init(start)) =>
            if ((pos - start).norm >= pinchTreshold) {
              // Log("Started drag")
              state = Drag
              dragInitEvents += DragEvent(pos - trans)
              dragInitEvents.foreach(widget.event)
            } else {
              dragInitEvents += DragEvent(pos - trans)
            }
          case (Move, 2, Pinch) =>
            widget.event(PinchEvent(Move, touches))
          case (Move, _, Drag) =>
            widget.event(DragEvent(pos - trans))
          case (typ, size, state) => Log(s"-- Ignore touch $typ, $size, $size")
        }
      case _ =>
    }

  }

}
