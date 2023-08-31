package space.nexel.aether.jvm.graphics

import space.nexel.aether.core.buffers.Var
import space.nexel.aether.core.types.*
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL12._
import org.lwjgl.opengl.GL13._
import org.lwjgl.opengl.GL14._
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL21._
import org.lwjgl.opengl.GL30._
import org.lwjgl.opengl.GL31._
import org.lwjgl.opengl.GL41._

class ShaderAttribute(
  val index: Int,
  val name: String,
  val size: Int,
  val aType: Int) extends Var {
  
  var debugSize: Int = 0

  def typeHex = aType.toHexString
  def typeName = GlUtil.constantTypeIndex(aType)
  private[graphics] var value: Any = _

  override def toString(): String = s"[Attribute $index: $name, $typeName[$typeHex], $size, value: $value]"

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

  override def putI(x: Int) = {
    glVertexAttrib1f(index, x.toFloat)
    value = x
  }
  override def put2I(x: Int, y: Int) = {
    glVertexAttrib2f(index, x.toFloat, y.toFloat)
    value = (x, y)
  }
  override def put3I(x: Int, y: Int, z: Int) = {
    glVertexAttrib3f(index, x.toFloat, y.toFloat, z.toFloat)
    value = (x, y, z)
  }
  override def put4I(x: Int, y: Int, z: Int, w: Int) = {
    glVertexAttrib4f(index, x.toFloat, y.toFloat, z.toFloat, w.toFloat)
    value = (x, y, z, w)
  }

  override def putIv(v: Array[Int]) = ???

  override def putF(x: Float) = {
    glVertexAttrib1f(index, x)
    value = x
  }
  override def put2F(x: Float, y: Float) = {
    glVertexAttrib2f(index, x, y)
    value = (x, y)
  }
  override def put3F(x: Float, y: Float, z: Float) = {
    glVertexAttrib3f(index, x, y, z)
    value = (x, y, z)
  }
  override def put4F(x: Float, y: Float, z: Float, w: Float) = {
    glVertexAttrib4f(index, x, y, z, w)
    value = (x, y, z, w)
  }

//  override def putD(x: Double) = ??? //glVertexAttrib1d(index, x)
//  override def put2D(x: Double, y: Double) = ??? //glVertexAttrib2d(index, x, y)
//  override def put3D(x: Double, y: Double, z: Double) = ??? //glVertexAttrib3d(index, x, y, z)
//  override def put4D(x: Double, y: Double, z: Double, w: Double) = ??? //glVertexAttrib4d(index, x, y, z, w)

  override def putMat2F(v: Mat2F) = ???
  override def putMat3F(v: Mat3F) = ???
  override def putMat4F(v: Mat4F) = ???

}
