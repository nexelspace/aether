package aether.lib.visual

import aether.core.types.Vec2F
import Visual.RenderEvent
import aether.core.types.RectF
import aether.lib.canvas.Canvas
import aether.core.types.Vec2I
import aether.core.platform.Event

object Visual {
  trait RenderEvent(viewport: RectF) extends Event
}

/** Graphical 2D object with size. */
trait Visual {
  /** Size of visual in pixels or zero if visual has no definite size. */
  def size: Vec2F

  //TODO: Implement paint with transform
  // def paint(canvas: Canvas, transform: Tx2FAxis, viewport: Vec2F): Unit
  def paint(canvas: Canvas): Unit

  // def event(event: RenderEvent): Unit
}

