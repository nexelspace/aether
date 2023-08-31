package space.nexel.aether.core.graphics

import space.nexel.aether.core.types.Vec2F
import space.nexel.aether.core.types.Vec2I

import space.nexel.aether.core.graphics.Graphics
import space.nexel.aether.core.platform.Resource
import space.nexel.aether.core.platform.Event
import space.nexel.aether.core.platform.NativeResource
import space.nexel.aether.core.graphics.Graphics.RenderTarget
import Display.Config
import space.nexel.aether.core.platform.Platform

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


  def apply(config: Config)(using platform: Platform): Display = platform.displayFactory.create(config)
  def apply(size: Vec2I)(using platform: Platform): Display = platform.displayFactory.create(Config(size = size))

}

trait Display extends NativeResource[Display, Display.Config] with RenderTarget {
  val config: Config
  def graphics: Graphics
  def size: Vec2I

  def grabPointer(grab: Boolean): Unit

  def render(callback: (Display) => Unit): Unit

}
