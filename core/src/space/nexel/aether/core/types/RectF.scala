package space.nexel.aether.core.types

import space.nexel.aether.core.types.Vec2F
import space.nexel.aether.core.types.Vec4F

object RectF {
  def apply(x: Float, y: Float, sizeX: Float, sizeY: Float) = new RectF(x, y, sizeX, sizeY)
  def apply(pos: Vec2F, size: Vec2F) = new RectF(pos.x, pos.y, size.x, size.y)
  def apply(size: Vec2F) = new RectF(0, 0, size.x, size.y)
  def fromBounds(x: Float, y: Float, x2: Float, y2: Float) = new RectF(x, y, x2-x, y2-y)
  val unit = apply(0, 0, 1, 1)
}

class RectF(
  val x: Float, 
  val y: Float, 
  val sizeX: Float, 
  val sizeY: Float) {

  def x2 = x + sizeX
  def y2 = y + sizeY
  
  def begin = Vec2F(x, y)
  def end = Vec2F(x2, y2)
  def center = Vec2F(x + sizeX / 2, y + sizeY / 2)
  def size = Vec2F(sizeX, sizeY)

  def expand(expand: Float) = RectF(x - expand, y - expand, sizeX + 2 * expand, sizeY + 2 * expand)
  def expand(expandH: Float, expandV: Float) = RectF(x - expandH, y - expandV, sizeX + 2 * expandH, sizeY + 2 * expandV)

  def translate(v: Vec2F) = RectF(x + v.x, y + v.y, sizeX, sizeY)
  def *(s: Float) = RectF(x * s, y * s, sizeX * s, sizeY * s)
  def *(v: Vec2F) = RectF(x * v.x, y * v.y, sizeX * v.x, sizeY * v.y)

  def isInside(p: Vec2F) = p.x >= x && p.y >= y && p.x < x + sizeX && p.y < y + sizeY
  def isInside(insideArea: RectF) = insideArea.x >= x && insideArea.y >= y && insideArea.x2 <= x2 && insideArea.y2 <= y2
  def clamp(p: Vec2F): Vec2F = {
    Vec2F(
      if (p.x < x) x else if (p.x > x2) x2 else p.x,
      if (p.y < y) y else if (p.y > y2) y2 else p.y)
  }
  def intersect(rect: RectF) = {
    RectF.fromBounds(Math.max(x, rect.x), Math.max(y, rect.y), Math.min(x2, rect.x2), Math.min(y2, rect.y2))
  }
  def union(rect: RectF) = ???
  override def toString = s"[$x, $y, $sizeX, $sizeY]"
  def toVec4F: Vec4F = Vec4F(x, y, sizeX, sizeY)
  def toRectI = RectI(x.toInt, y.toInt, sizeX.toInt, sizeY.toInt)

}