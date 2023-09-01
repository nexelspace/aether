package aether.js.graphics

import aether.core.types.*
import aether.core.buffers.Var
import org.scalajs.dom.raw.{WebGLRenderingContext => GL}

class ShaderAttribute(
                       val index: Int,
                       val name: String,
                       val size: Int,
                       val aType: Int)(using gl: GL) extends Var {

  //var debugSize: Int = 0

  override def toString(): String = s"[Attribute $index, $name, $size, ${aType.toHexString}]"

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

  override def putI(x: Int) = gl.vertexAttrib1f(index, x)
  override def put2I(x: Int, y: Int) = gl.vertexAttrib2f(index, x, y)
  override def put3I(x: Int, y: Int, z: Int) = gl.vertexAttrib3f(index, x, y, z)
  override def put4I(x: Int, y: Int, z: Int, w: Int) = gl.vertexAttrib4f(index, x, y, z, w)

  override def putIv(v: Array[Int]) = ???
    
  override def putF(x: Float) = gl.vertexAttrib1f(index, x)
  override def put2F(x: Float, y: Float) = gl.vertexAttrib2f(index, x, y)
  override def put3F(x: Float, y: Float, z: Float) = gl.vertexAttrib3f(index, x, y, z)
  override def put4F(x: Float, y: Float, z: Float, w: Float) = gl.vertexAttrib4f(index, x, y, z, w)

  override def putMat2F(v: Mat2F) = ???
  override def putMat3F(v: Mat3F) = ???
  override def putMat4F(v: Mat4F) = ???

}
