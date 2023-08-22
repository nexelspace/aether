package space.nexel.aether.core.types

import space.nexel.aether.core.types.Vec3.Vec3F
import space.nexel.aether.core.types.Vec4.Vec4F

object Mat4F {
  def apply(a00: Float, a10: Float, a20: Float, a30: Float,
            a01: Float, a11: Float, a21: Float, a31: Float,
            a02: Float, a12: Float, a22: Float, a32: Float,
            a03: Float, a13: Float, a23: Float, a33: Float) =
    new Mat4F(
      a00, a10, a20, a30,
      a01, a11, a21, a31,
      a02, a12, a22, a32,
      a03, a13, a23, a33)

  def apply(a: Array[Float]) = new Mat4F(
    a(0x0), a(0x1), a(0x2), a(0x3),
    a(0x4), a(0x5), a(0x6), a(0x7),
    a(0x8), a(0x9), a(0xa), a(0xb),
    a(0xc), a(0xd), a(0xe), a(0xf))

  def diagonal(s: Float) = new Mat4F(s, 0, 0, 0, 0, s, 0, 0, 0, 0, s, 0, 0, 0, 0, s)

  val identity = diagonal(1)

  def fromTransform(mat: Mat3F, translate: Vec3F): Mat4F = {
    Mat4F(
      mat.a00, mat.a01, mat.a02, translate.x,
      mat.a10, mat.a11, mat.a12, translate.y,
      mat.a20, mat.a21, mat.a22, translate.z,
      0, 0, 0, 0)

  }
}

final class Mat4F(
  val a00: Float, val a10: Float, val a20: Float, val a30: Float,
  val a01: Float, val a11: Float, val a21: Float, val a31: Float,
  val a02: Float, val a12: Float, val a22: Float, val a32: Float,
  val a03: Float, val a13: Float, val a23: Float, val a33: Float) {

  def transpose: Mat4F = new Mat4F(
    a00, a01, a02, a03,
    a10, a11, a12, a13,
    a20, a21, a22, a23,
    a30, a31, a32, a33)

  def +(m: Mat4F) = new Mat4F(
    a00 + m.a00, a10 + m.a10, a20 + m.a20, a30 + m.a30,
    a01 + m.a01, a11 + m.a11, a21 + m.a21, a31 + m.a31,
    a02 + m.a02, a12 + m.a12, a22 + m.a22, a32 + m.a32,
    a03 + m.a03, a13 + m.a13, a23 + m.a23, a33 + m.a33)

  def -(m: Mat4F) = new Mat4F(
    a00 - m.a00, a10 - m.a10, a20 - m.a20, a30 - m.a30,
    a01 - m.a01, a11 - m.a11, a21 - m.a21, a31 - m.a31,
    a02 - m.a02, a12 - m.a12, a22 - m.a22, a32 - m.a32,
    a03 - m.a03, a13 - m.a13, a23 - m.a23, a33 - m.a33)

  def *(s: Float) = new Mat4F(
    a00 * s, a10 * s, a20 * s, a30 * s,
    a01 * s, a11 * s, a21 * s, a31 * s,
    a02 * s, a12 * s, a22 * s, a32 * s,
    a03 * s, a13 * s, a23 * s, a33 * s)

  def /(s: Float) = new Mat4F(
    a00 / s, a10 / s, a20 / s, a30 / s,
    a01 / s, a11 / s, a21 / s, a31 / s,
    a02 / s, a12 / s, a22 / s, a32 / s,
    a03 / s, a13 / s, a23 / s, a33 / s)

  def *(v: Vec4F) = new Vec4F(
    a00 * v.x + a10 * v.y + a20 * v.z + a30 * v.w,
    a01 * v.x + a11 * v.y + a21 * v.z + a31 * v.w,
    a02 * v.x + a12 * v.y + a22 * v.z + a32 * v.w,
    a03 * v.x + a13 * v.y + a23 * v.z + a33 * v.w)

  /**
   * Multiplication interprets vales as column-major.
   */
  def *(m: Mat4F) = new Mat4F(
    a00 * m.a00 + a01 * m.a10 + a02 * m.a20 + a03 * m.a30,
    a10 * m.a00 + a11 * m.a10 + a12 * m.a20 + a13 * m.a30,
    a20 * m.a00 + a21 * m.a10 + a22 * m.a20 + a23 * m.a30,
    a30 * m.a00 + a31 * m.a10 + a32 * m.a20 + a33 * m.a30,
    a00 * m.a01 + a01 * m.a11 + a02 * m.a21 + a03 * m.a31,
    a10 * m.a01 + a11 * m.a11 + a12 * m.a21 + a13 * m.a31,
    a20 * m.a01 + a21 * m.a11 + a22 * m.a21 + a23 * m.a31,
    a30 * m.a01 + a31 * m.a11 + a32 * m.a21 + a33 * m.a31,
    a00 * m.a02 + a01 * m.a12 + a02 * m.a22 + a03 * m.a32,
    a10 * m.a02 + a11 * m.a12 + a12 * m.a22 + a13 * m.a32,
    a20 * m.a02 + a21 * m.a12 + a22 * m.a22 + a23 * m.a32,
    a30 * m.a02 + a31 * m.a12 + a32 * m.a22 + a33 * m.a32,
    a00 * m.a03 + a01 * m.a13 + a02 * m.a23 + a03 * m.a33,
    a10 * m.a03 + a11 * m.a13 + a12 * m.a23 + a13 * m.a33,
    a20 * m.a03 + a21 * m.a13 + a22 * m.a23 + a23 * m.a33,
    a30 * m.a03 + a31 * m.a13 + a32 * m.a23 + a33 * m.a33)

  def get(a: Array[Float]) = {
    a(0x0) = a00
    a(0x1) = a10
    a(0x2) = a20
    a(0x3) = a30
    a(0x4) = a01
    a(0x5) = a11
    a(0x6) = a21
    a(0x7) = a31
    a(0x8) = a02
    a(0x9) = a12
    a(0xa) = a22
    a(0xb) = a32
    a(0xc) = a03
    a(0xd) = a13
    a(0xe) = a23
    a(0xf) = a33
  }

  def get(a: Array[Float], index: Int, stride: Int) = {
    a(index + 0 * stride + 0) = a00
    a(index + 0 * stride + 1) = a10
    a(index + 0 * stride + 2) = a20
    a(index + 0 * stride + 3) = a30
    a(index + 1 * stride + 0) = a01
    a(index + 1 * stride + 1) = a11
    a(index + 1 * stride + 2) = a21
    a(index + 1 * stride + 3) = a31
    a(index + 2 * stride + 0) = a02
    a(index + 2 * stride + 1) = a12
    a(index + 2 * stride + 2) = a22
    a(index + 2 * stride + 3) = a32
    a(index + 3 * stride + 0) = a03
    a(index + 3 * stride + 1) = a13
    a(index + 3 * stride + 2) = a23
    a(index + 3 * stride + 3) = a33
  }

  override def toString = s"[$a00, $a10, $a20, $a30, $a01, $a11, $a21, $a31, $a02, $a12, $a22, $a32, $a03, $a13, $a23, $a33]"

  def toMat3F() = Mat3F(a00, a10, a20, a11, a11, a11, a22, a22, a22)
}