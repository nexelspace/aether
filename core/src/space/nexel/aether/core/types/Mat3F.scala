package space.nexel.aether.core.types

import space.nexel.aether.core.types.Vec3.*

object Mat3F {
  def apply(
    a00: Float, a10: Float, a20: Float,
    a01: Float, a11: Float, a21: Float,
    a02: Float, a12: Float, a22: Float) =
    new Mat3F(
      a00, a10, a20,
      a01, a11, a21,
      a02, a12, a22)

  def apply(a: Array[Float]) = new Mat3F(
    a(0), a(1), a(2),
    a(3), a(4), a(5),
    a(6), a(7), a(8))

  def apply(a: Array[Float], index: Int, stride: Int) = new Mat3F(
    a(index + 0 * stride + 0), a(index + 0 * stride + 1), a(index + 0 * stride + 2),
    a(index + 1 * stride + 0), a(index + 1 * stride + 1), a(index + 1 * stride + 2),
    a(index + 2 * stride + 0), a(index + 2 * stride + 1), a(index + 2 * stride + 2))

  def diagonal(s: Float) = new Mat3F(s, 0, 0, 0, s, 0, 0, 0, s)
  def diagonal(x: Float, y: Float, z: Float) = new Mat3F(x, 0, 0, 0, y, 0, 0, 0, z)
  val identity = diagonal(1)
}

final class Mat3F(
  val a00: Float, val a10: Float, val a20: Float,
  val a01: Float, val a11: Float, val a21: Float,
  val a02: Float, val a12: Float, val a22: Float) {

  def +(m: Mat3F) = new Mat3F(
    a00 + m.a00, a10 + m.a10, a20 + m.a20,
    a01 + m.a01, a11 + m.a11, a21 + m.a21,
    a02 + m.a02, a12 + m.a12, a22 + m.a22)

  def -(m: Mat3F) = new Mat3F(
    a00 - m.a00, a10 - m.a10, a20 - m.a20,
    a01 - m.a01, a11 - m.a11, a21 - m.a21,
    a02 - m.a02, a12 - m.a12, a22 - m.a22)

  def *(s: Float) = new Mat3F(
    a00 * s, a10 * s, a20 * s,
    a01 * s, a11 * s, a21 * s,
    a02 * s, a12 * s, a22 * s)

  def /(s: Float) = new Mat3F(
    a00 / s, a10 / s, a20 / s,
    a01 / s, a11 / s, a21 / s,
    a02 / s, a12 / s, a22 / s)

  def *(v: Vec3F) = new Vec3F(
    a00 * v.x + a01 * v.y + a02 * v.z,
    a10 * v.x + a11 * v.y + a12 * v.z,
    a20 * v.x + a21 * v.y + a22 * v.z)

  def *(m: Mat3F) = new Mat3F(
    a00 * m.a00 + a01 * m.a10 + a02 * m.a20,
    a10 * m.a00 + a11 * m.a10 + a12 * m.a20,
    a20 * m.a00 + a21 * m.a10 + a22 * m.a20,
    a00 * m.a01 + a01 * m.a11 + a02 * m.a21,
    a10 * m.a01 + a11 * m.a11 + a12 * m.a21,
    a20 * m.a01 + a21 * m.a11 + a22 * m.a21,
    a00 * m.a02 + a01 * m.a12 + a02 * m.a22,
    a10 * m.a02 + a11 * m.a12 + a12 * m.a22,
    a20 * m.a02 + a21 * m.a12 + a22 * m.a22)

  def get(a: Array[Float]) = {
    a(0) = a00; a(1) = a10; a(2) = a20
    a(3) = a01; a(4) = a11; a(5) = a21
    a(6) = a02; a(7) = a12; a(8) = a22
  }

  def get(a: Array[Float], index: Int, stride: Int) = {
    a(index + 0 * stride + 0) = a00; a(index + 0 * stride + 1) = a10; a(index + 0 * stride + 2) = a20
    a(index + 1 * stride + 0) = a01; a(index + 1 * stride + 1) = a11; a(index + 1 * stride + 2) = a21
    a(index + 2 * stride + 0) = a02; a(index + 2 * stride + 1) = a12; a(index + 2 * stride + 2) = a22
  }
  
  override def toString: String = s"[$a00 $a10 $a20 | $a01 $a11 $a21 | $a02 $a12 $a22]"
  
}