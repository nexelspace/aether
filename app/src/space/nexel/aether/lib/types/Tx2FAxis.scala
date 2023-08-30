package space.nexel.aether.lib.types

import space.nexel.aether.core.types.*

object Tx2FAxis {

  def apply(scale: Vec2F, trans: Vec2F) = new Tx2FAxis(scale, trans)
  def scale(scale: Float) = new Tx2FAxis(Vec2F(scale), Vec2F(0))

  val Identity = Tx2FAxis(Vec2F(1), Vec2F(0))

}

/**
 * Axis-aligned 2D float transform.
 */
case class Tx2FAxis(scale: Vec2F, trans: Vec2F) {

  def *(t: Tx2FAxis): Tx2FAxis = {
    Tx2FAxis(scale * t.scale, scale * t.trans + trans)
  }

  def *(v: Vec2F): Vec2F = {
    scale * v + trans
  }
  
  def inv: Tx2FAxis = {
    val inv = Vec2F(1) / scale
    Tx2FAxis(inv, -inv * trans)
  }

  def transformArea(area: RectF): RectF = {
    RectF(scale * area.begin + trans, scale * area.size)
  }

  def translated(x: Float, y: Float): Tx2FAxis = Tx2FAxis(scale, trans + scale * Vec2F(x, y))
  def translated(v: Vec2F): Tx2FAxis = Tx2FAxis(scale, trans + scale * v)
  def scaled(s: Float): Tx2FAxis = Tx2FAxis(scale * s, trans)
  def scaled(s: Vec2F): Tx2FAxis = Tx2FAxis(scale * s, trans)
  def scaled(sx: Float, sy: Float): Tx2FAxis = scaled(Vec2F(sx, sy))

  def toMat3F = Mat3F(scale.x, 0, 0, 0, scale.y, 0, trans.x, trans.y, 1)
  // def toMat4F = Mat4F(scale.x, 0, 0, 0, 0, scale.y, 0, 0, 0, 0, 1, 0, trans.x, trans.y, 0, 1)

}