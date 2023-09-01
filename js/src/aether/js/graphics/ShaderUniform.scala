package aether.js.graphics

import aether.core.buffers.Var
import aether.core.types.*
import org.scalajs.dom.raw.WebGLProgram
import org.scalajs.dom.raw.WebGLUniformLocation

import scala.scalajs.js
import scala.scalajs.js.typedarray.Float32Array
import scala.scalajs.js.typedarray.Int32Array
import org.scalajs.dom.raw.{WebGLRenderingContext => GL}

import js.JSConverters._
import ShaderUniform._

object ShaderUniform {
  val array4 = new Float32Array(4)
  val array9 = new Float32Array(9)
  val array16 = new Float32Array(16)
}

class ShaderUniform(
  val program:  WebGLProgram,
  val location: WebGLUniformLocation,
  val size:     Int,
  val `type`:   Int)(using gl: GL) extends Var {

  override def toString(): String = s"[Uniform $location, $size, ${`type`.toHexString}]"

  override def putI(x: Int) = {
    gl.useProgram(program)
    gl.uniform1i(location, x)
  }

  override def put2I(x: Int, y: Int) = {
    gl.useProgram(program)
    gl.uniform2i(location, x, y)
  }

  override def put3I(x: Int, y: Int, z: Int) = {
    gl.useProgram(program)
    gl.uniform3i(location, x, y, z)
  }

  override def put4I(x: Int, y: Int, z: Int, w: Int) = {
    gl.useProgram(program)
    gl.uniform4i(location, x, y, z, w)
  }

  override def putIv(v: Array[Int]) = {
    gl.useProgram(program)
    gl.uniform1iv(location, v.toJSArray)
  }

  override def putF(value: Float) = {
    gl.useProgram(program)
    gl.uniform1f(location, value)
  }

  override def put2F(x: Float, y: Float) = {
    gl.useProgram(program)
    gl.uniform2f(location, x, y)
  }

  override def put3F(x: Float, y: Float, z: Float) = {
    gl.useProgram(program)
    gl.uniform3f(location, x, y, z)
  }

  override def put4F(x: Float, y: Float, z: Float, w: Float) = {
    gl.useProgram(program)
    gl.uniform4f(location, x, y, z, w)
  }

  override def putMat2F(value: Mat2F) = {
    JsUtil.toArray(value, array4)
    gl.useProgram(program)
    gl.uniformMatrix2fv(location, false, array4)
  }

  override def putMat3F(value: Mat3F) = {
    JsUtil.toArray(value, array9)
    gl.useProgram(program)
    gl.uniformMatrix3fv(location, false, array9)
  }

  override def putMat4F(value: Mat4F) = {
    JsUtil.toArray(value, array16)
    gl.useProgram(program)
    gl.uniformMatrix4fv(location, false, array16)
  }

  override def getI(): Int = ???
  override def get2I(): Vec2I = ???
  override def get3I(): Vec3I = ???
  override def get4I(): Vec4I = ???
  override def getF(): Float = ???
  override def get2F(): Vec2F = ???
  override def get3F(): Vec3F = ???
  override def get4F(): Vec4F = ???
  override def getMat2F(): Mat2F = ???
  override def getMat3F(): Mat3F = ???
  override def getMat4F(): Mat4F = ???
}