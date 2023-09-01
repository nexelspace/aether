package aether.lib.types

import aether.core.types.Mat2F
import aether.core.types.Mat4F
import aether.core.types.Vec2F
import aether.core.types.RectF

object Transform2F {
  def identity = Transform2F(Mat2F.identity, Vec2F.Zero)
  def translation(x: Float, y: Float) = Transform2F(Mat2F.identity, Vec2F(x, y))
  def rotation(rads: Float) = new Transform2F(Mat2F.rotation(rads), Vec2F.Zero)

  def apply(mat: Mat2F, trans: Vec2F) = new Transform2F(mat, trans)
  // def apply(trans: TransformM2F) = new Transform2F(trans.mat, trans.trans)
}

case class Transform2F(mat: Mat2F, trans: Vec2F) {

  //def scale: Vec2F = Vec2F(mat.a00, mat.a01) //TODO

  def *(t: Transform2F): Transform2F = {
    Transform2F(mat * t.mat, mat * t.trans + trans)
  }

  def *(v: Vec2F): Vec2F = {
    mat * v + trans
  }

  def inv: Transform2F = {
    val inv = mat.inv
    Transform2F(inv, inv * -trans)
  }
  
  def inv(v: Vec2F): Vec2F = {
    mat.inv * (v - trans)
  }

  def transformArea(area: RectF): RectF = {
    val pos = mat * area.begin + trans
    val size = mat * area.size
    RectF(pos, size)
  }

  def translate(x: Float, y: Float): Transform2F = Transform2F(mat, trans + mat * Vec2F(x, y))
  def scale(s: Float): Transform2F = Transform2F(mat * s, trans)
  def scale(s: Vec2F): Transform2F = Transform2F(mat *| s, trans)
  def scale(sx: Float, sy: Float): Transform2F = scale(Vec2F(sx, sy))

  def rotate(rads: Float) = {
    Transform2F(mat * Mat2F.rotation(rads), mat * trans)
  }

  def toMat4f = Mat4F(mat.a00, mat.a10, 0, 0, mat.a01, mat.a11, 0, 0, 0, 0, 1, 0, trans.x, trans.y, 0, 1)

  override def toString = mat.toString + trans.toString
}