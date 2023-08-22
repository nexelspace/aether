package space.nexel.aether.core.types

import space.nexel.aether.core.math.VMathF
import space.nexel.aether.core.types.Vec2.*

object Mat2F {
  def apply(
    a00: Float, a10: Float,
    a01: Float, a11: Float) = new Mat2F(
    a00, a10,
    a01, a11)

  val identity = diagonal(1)
  def diagonal(s: Float) = new Mat2F(s, 0, 0, s)
  def diagonal(x: Float, y: Float, z: Float) = new Mat2F(x, 0, 0, y)
  def rotation(rads: Float) = {
    val cos = VMathF.cos(rads)
    val sin = VMathF.sin(rads)
    Mat2F(cos, sin, -sin, cos)
  }
}

case class Mat2F(
  a00: Float, a10: Float,
  a01: Float, a11: Float) {

  def det = a00 * a11 - a10 * a01

  def inv = {
    val d = det
    new Mat2F(a11 * d, -a01 * d, -a10 * d, a00 * d)
  }

  def +(m: Mat2F) = new Mat2F(
    a00 + m.a00, a10 + m.a10,
    a01 + m.a01, a11 + m.a11)

  def -(m: Mat2F) = new Mat2F(
    a00 - m.a00, a10 - m.a10,
    a01 - m.a01, a11 - m.a11)

  def *(s: Float) = new Mat2F(
    a00 * s, a10 * s,
    a01 * s, a11 * s)

  def /(s: Float) = new Mat2F(
    a00 / s, a10 / s,
    a01 / s, a11 / s)

  def *(v: Vec2F) = new Vec2F(
    a00 * v.x + a01 * v.y,
    a10 * v.x + a11 * v.y)

  def *(m: Mat2F) = new Mat2F(
    a00 * m.a00 + a01 * m.a10,
    a10 * m.a00 + a11 * m.a10,
    a00 * m.a01 + a01 * m.a11,
    a10 * m.a01 + a11 * m.a11)

  /** Componentwise multiplication. */
  def *|(v: Vec2F) = new Mat2F(
    a00 * v.x, a10 * v.y,
    a01 * v.x, a11 * v.y)

  def get(a: Array[Float]) = {
    a(0) = a00; a(1) = a10;
    a(2) = a01; a(3) = a11;
  }

  def get(a: Array[Float], index: Int, stride: Int) = {
    a(index + 0 * stride + 0) = a00; a(index + 0 * stride + 1) = a10;
    a(index + 1 * stride + 0) = a01; a(index + 1 * stride + 1) = a11;
  }

  override def toString: String = s"[$a00, $a10, $a01, $a11]"

}