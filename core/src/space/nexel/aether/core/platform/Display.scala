package space.nexel.aether.core.platform

import space.nexel.aether.core.types.Vec2F
import space.nexel.aether.core.types.Vec2I

import space.nexel.aether.core.graphics.Graphics

object Display {
  type DisplayFactory = Resource.Factory[Display, Config]

  case class Config(
      size: Vec2I = Vec2I(0),
      fullscreen: Boolean = false,
      id: Option[String] = None,
      windowTitle: String = ""
  ) extends Resource.Config

  trait DisplayEvent extends Event {
    val display: Display
  }
  case class Paint(display: Display) extends DisplayEvent
  case class Focus(display: Display, focused: Boolean) extends DisplayEvent
  case class Resize(display: Display, size: Vec2I) extends DisplayEvent
}

trait Display extends NativeResource[Display, Display.Config] {
  def graphics: Graphics
  def size: Vec2I

  def grabPointer(grab: Boolean): Unit

  // def render(callback: => Unit): Unit

}