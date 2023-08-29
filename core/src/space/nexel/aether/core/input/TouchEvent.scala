package space.nexel.aether.core.input

import space.nexel.aether.core.platform.Event
import space.nexel.aether.core.types.Vec2F

import TouchEvent.*

object TouchEvent {
  enum Type { case Start, End, Move}
}
case class TouchEvent(typ: Type, id: Int, pos: Vec2F, touches: Map[Int, Vec2F]) extends Event
