package aether.lib.quad

import scala.reflect.ClassTag
import aether.core.types.RectI
import aether.core.types.Vec2I
import aether.core.types.VecExt.*

class ArrayGrid[T: ClassTag](val defaultValue: T, val size: Int) extends MutableGrid[T] {
  val array: Array[T] = Array.tabulate[T](size * size)(_ => defaultValue)

  override def bounds: RectI = RectI(0, 0, size, size)

  override def apply(p: Vec2I): T = {
    if (p.inRange(size)) array(p.index(size)) else defaultValue
  }

  override def update(p: Vec2I, value: T): Unit = {
    if (p.inRange(size)) array(p.index(size)) = value
  }
}
