package space.nexel.aether.core.input

import space.nexel.aether.core.types.Vec2F
import space.nexel.aether.core.platform.Event

abstract class PointerEvent() extends Event {
  val pos: Vec2F
}

object PointerEvent {
  
  object MouseButton {
    val Left = 1
    val Middle = 2
    val Right = 3

  }

  case class MouseButton(val pressed: Boolean, pos: Vec2F, button: Int) extends PointerEvent {
  }

  case class MouseWheel(pos: Vec2F, wheel: Float) extends PointerEvent {
  }

  case class MouseMove(pressed: Boolean, pos: Vec2F, button: Int) extends PointerEvent {
  }

  case class MouseRelative(pos: Vec2F, button: Int) extends PointerEvent {
  }
}
