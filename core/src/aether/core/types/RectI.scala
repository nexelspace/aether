package aether.core.types

object RectI {
  def apply(x: Int, y: Int, sizeX: Int, sizeY: Int) = new RectI(x, y, sizeX, sizeY)
  def apply(pos: Vec2I, size: Vec2I) = new RectI(pos.x, pos.y, size.x, size.y)
  val Zero = apply(0, 0, 0, 0)
  val Unit = apply(0, 0, 1, 1)
}

class RectI(
    val x: Int, 
    val y: Int, 
    val sizeX: Int, 
    val sizeY: Int) {

  def pos = Vec2I(x, y)
  def size = Vec2I(sizeX, sizeY)

  def x2 = x + sizeX
  def y2 = y + sizeY

  def isInside(p: Vec2I) = p.x >= y && p.y >= y && p.x < x + sizeX && p.y < y + sizeY

  override def toString = s"[$x, $y, $sizeX, $sizeY]"

  def bounds: Vec4I = Vec4I(x, y, x+sizeX, y+sizeY)
  def toRectF = RectF(x.toFloat, y.toFloat, sizeX.toFloat, sizeY.toFloat)
  def toVec4I: Vec4I = Vec4I(x, y, sizeX, sizeY)

  def rangeY: Range = y until y + sizeY
  def rangeX: Range = x until x + sizeX
}