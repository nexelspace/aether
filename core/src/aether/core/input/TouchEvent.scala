package aether.core.input

import aether.core.platform.Event
import aether.core.types.Vec2F

import TouchEvent.*

object TouchEvent {
  enum Type { case Start, End, Move}
}
case class TouchEvent(typ: Type, id: Int, pos: Vec2F, touches: Map[Int, Vec2F]) extends Event
