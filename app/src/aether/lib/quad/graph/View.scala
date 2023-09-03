package aether.lib.quad.graph

import aether.core.types.RectF
import aether.core.types.Vec2F

object View {
  case class Disc(val origo: Vec2F, val radius: Float) extends View {
    def area = RectF(Vec2F(-radius, -radius), Vec2F(2*radius,2*radius))
  }
}

abstract class View {
  def area: RectF

}
