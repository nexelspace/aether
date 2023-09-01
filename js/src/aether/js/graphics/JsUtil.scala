package aether.js.graphics

import aether.core.types.Mat2F
import aether.core.types.Mat3F
import aether.core.types.Mat4F

import scala.scalajs.js.typedarray.Float32Array

object JsUtil {
  def toArray(mat: Mat2F, array: Float32Array) = {
    array(0) = mat.a00
    array(1) = mat.a10
    array(2) = mat.a01
    array(3) = mat.a11
  }

  def toArray(mat: Mat3F, array: Float32Array) = {
    array(0) = mat.a00
    array(1) = mat.a10
    array(2) = mat.a20
    array(3) = mat.a01
    array(4) = mat.a11
    array(5) = mat.a21
    array(6) = mat.a02
    array(7) = mat.a12
    array(8) = mat.a22
  }

  def toArray(mat: Mat4F, array: Float32Array) = {
    array(0x0) = mat.a00
    array(0x1) = mat.a10
    array(0x2) = mat.a20
    array(0x3) = mat.a30
    array(0x4) = mat.a01
    array(0x5) = mat.a11
    array(0x6) = mat.a21
    array(0x7) = mat.a31
    array(0x8) = mat.a02
    array(0x9) = mat.a12
    array(0xa) = mat.a22
    array(0xb) = mat.a32
    array(0xc) = mat.a03
    array(0xd) = mat.a13
    array(0xe) = mat.a23
    array(0xf) = mat.a33
  }
}
