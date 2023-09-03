package aether.lib.quad

import aether.core.types.Vec2I

trait MutableGrid[T] extends Grid2D[T] {

  def update(x: Int, y: Int, value: T): Unit = update(Vec2I(x, y), value)
  def update(p: Vec2I, value: T): Unit

}
