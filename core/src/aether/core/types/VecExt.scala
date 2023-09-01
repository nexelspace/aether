package aether.core.types

import aether.core.math.VMathF

/** Less canonical collection of Vec extensions.
  */
object VecExt {

  object V2I {
    def index(base: Int, index: Int): Vec2I = {
      Vec2I(index % base, (index / base) % base)
    }
    def index2(v: Int): Vec3I = Vec3I(v & 1, (v >> 1) & 1, (v >> 2) & 1)
  }

  object V3I {
    def index(base: Int, index: Int): Vec3I = {
      Vec3I(index % base, (index / base) % base, index / (base * base))
    }
    def index2(v: Int): Vec3I = Vec3I(v & 1, (v >> 1) & 1, (v >> 2) & 1)
  }

  extension (v: Vec2F) {
    def floorVec2I: Vec2I = Vec2I(v.x.floor.toInt, v.y.floor.toInt)
    def roundVec2I: Vec2I = Vec2I(v.x.round.toInt, v.y.round.toInt)
    def toVec2I: Vec2I = floorVec2I
    def toVec2D = Vec2D(v.x, v.y)
    def toVec3F = Vec3F(v.x, v.y, 0)
    def toTuple = (v.x, v.y)
  }

  extension (v: Vec2I) {
    def inRange(max: Int): Boolean = inRange(0, max)
    def inRange(min: Int, max: Int): Boolean = v.x >= min && v.x <= max && v.y >= 0 && v.y <= max
    def index(base: Int) = (v.y * base) + v.x
    def index2 = (v.y << 1) | v.x

  }

  extension (v: Vec3I) {
    def index(base: Int) = (v.z * base + v.y) * base + v.x
    def index2 = (v.z << 2) | (v.y << 1) | v.x

    def inRangeInclusive(min: Int, max: Int) =
      v.x >= min && v.x <= max && v.y >= min && v.y <= max && v.z >= min && v.z <= max
    def inRangeInclusive(min: Vec3I, max: Vec3I) =
      v.x >= min.x && v.x <= max.x && v.y >= min.y && v.y <= max.y && v.z >= min.z && v.z <= max.z

    def inRangeUntil(min: Vec3I, max: Vec3I) =
      v.x >= min.x && v.x < max.x && v.y >= min.y && v.y < max.y && v.z >= min.z && v.z < max.z

    def inRange(bounds: (Vec3I, Vec3I)) =
      v.x >= bounds._1.x && v.x < bounds._2.x &&
        v.y >= bounds._1.y && v.y < bounds._2.y &&
        v.z >= bounds._1.z && v.z < bounds._2.z

    def isZero() = v.x == 0 && v.y == 0 && v.z == 0
    def isOrthogonal() = v.x == 0 && v.y == 0 || v.y == 0 && v.z == 0 || v.z == 0 && v.x == 0
    def toVec3F = Vec3F(v.x.toFloat, v.y.toFloat, v.z.toFloat)
  }

  extension (v: Vec3F) {
    def max = VMathF.max(v.x, v.y, v.z)
    def min = VMathF.min(v.x, v.y, v.z)

    def floor = Vec3F(v.x.floor, v.y.floor, v.z.floor)
    def floorInt = Vec3I(v.x.floor.toInt, v.y.floor.toInt, v.z.floor.toInt)
    def round = Vec3I(v.x.round, v.y.round, v.z.round)
    def inRange(bounds: (Vec3F, Vec3F)) =
      v.x >= bounds._1.x && v.x < bounds._2.x &&
        v.y >= bounds._1.y && v.y < bounds._2.y &&
        v.z >= bounds._1.z && v.z < bounds._2.z
    def inRange(min: Float, max: Float) =
      v.x >= min && v.x <= max && v.y >= min && v.y <= max && v.z >= min && v.z <= max

    def signInt = Vec3I(if (v.x < 0) -1 else 1, if (v.y < 0) -1 else 1, if (v.z < 0) -1 else 1)
    def stepInt = Vec3I(if (v.x < 0) 0 else 1, if (v.y < 0) 0 else 1, if (v.z < 0) 0 else 1)

    def toVec3I = Vec3I(v.x.toInt, v.y.toInt, v.z.toInt)

    def rotateX(rad: Float): Vec3F = {
      val cos = VMathF.cos(rad)
      val sin = VMathF.sin(rad)
      Vec3F(v.x, cos * v.y - sin * v.z, sin * v.y + cos * v.z)
    }

    def rotateY(rad: Float): Vec3F = {
      val cos = VMathF.cos(rad)
      val sin = VMathF.sin(rad)
      Vec3F(cos * v.x - sin * v.z, v.y, sin * v.x + cos * v.z)
    }

    def rotateZ(rad: Float): Vec3F = {
      val cos = VMathF.cos(rad)
      val sin = VMathF.sin(rad)
      Vec3F(cos * v.x - sin * v.y, sin * v.x + cos * v.y, v.z)
    }
  }

  extension (v: Vec4I) {
    def toVec4F: Vec4F = Vec4F(v.x.toFloat, v.y.toFloat, v.z.toFloat, v.w.toFloat)
  }
}
