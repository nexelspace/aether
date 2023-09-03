package aether.lib.quad

import aether.core.types.RectI
import aether.core.types.Vec2I


trait Grid2D[T] {

  def bounds: RectI

  def apply(x: Int, y: Int): T = apply(Vec2I(x, y))
  def apply(p: Vec2I): T

  def get(area: RectI, target: Array[T], offset: Int = 0, stride: Int = 0) = {
    val yStep = if (stride == 0) 0 else stride - area.sizeX
    var i = offset
    for (y <- area.rangeY) {
      for (x <- area.rangeX) {
        target(i) = apply(x, y)
        i += 1
      }
      i += yStep
    }
  }

  // def newArray[T](size: Int): Array[T] = new Array[T](size)

  def trimBounds[T](trimElement: T): RectI = {
    var b = bounds
    // val array = newArray
    var continue = true
    while (continue) {
      continue = false
      // left
      if (b.rangeX.forall(apply(_, b.y) == trimElement)) {
        b = RectI(b.x, b.y + 1, b.sizeX, b.sizeY - 1)
        continue = true
      }
      // right
      if (b.rangeX.forall(apply(_, b.y + b.sizeY - 1) == trimElement)) {
        b = RectI(b.x, b.y, b.sizeX, b.sizeY - 1)
        continue = true
      }
      // top
      if (b.rangeY.forall(apply(b.x, _) == trimElement)) {
        b = RectI(b.x + 1, b.y, b.sizeX - 1, b.sizeY)
        continue = true
      }
      // bottom
      if (b.rangeY.forall(apply(b.x + b.sizeX - 1, _) == trimElement)) {
        b = RectI(b.x, b.y, b.sizeX - 1, b.sizeY)
        continue = true
      }
    }
    if (b.sizeX == 0 || b.sizeY == 0) RectI(bounds.x, bounds.y, 0, 0) else b
  }

  //  def getArray(area: RectI): Array[Array[T]] = {
  //    area.rangeY.map { y =>
  //      area.rangeX.map(x => this(x, y)).toArray
  //    }.toArray
  //  }

}
