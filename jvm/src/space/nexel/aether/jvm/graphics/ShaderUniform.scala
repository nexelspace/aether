package space.nexel.aether.jvm.graphics

import space.nexel.aether.core.platform._
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
import space.nexel.aether.core.buffers.Var
import space.nexel.aether.core.platform.Log
import space.nexel.aether.core.util.Strings

class ShaderUniform(
  val name:        String,
  val program:     Int,
  val uniform:     Int,
  val size:        Int,
  val typ:         Int,
  val arrayLength: Int) extends Var {

  def typeHex = typ.toHexString
  def typeName = GlUtil.constantTypeIndex.get(typ).getOrElse(typeHex)

  private[graphics] var value: Any = _

  override def toString(): String = s"[Uniform $uniform: $name, $typeName[$typeHex], $size, $arrayLength, value: $value]"

  override def putI(x: Int) = {
    glUseProgram(program)
    // if (typ)
    glUniform1i(uniform, x)
    checkError()
    value = x
  }

  override def put2I(x: Int, y: Int) = {
    glUseProgram(program)
    glUniform2i(uniform, x, y)
    checkError()
    value = (x, y)
  }

  override def put3I(x: Int, y: Int, z: Int) = {
    glUseProgram(program)
    glUniform3i(uniform, x, y, z)
    checkError()
    value = (x, y, z)
  }

  override def put4I(x: Int, y: Int, z: Int, w: Int) = {
    glUseProgram(program)
    glUniform4i(uniform, x, y, z, w)
    checkError()
    value = (x, y, z, w)
  }

  override def putIv(v: Array[Int]) = {
    glUseProgram(program)
    glUniform1iv(uniform, v)
    checkError()
    value = v.toSeq
  }

  override def putF(x: Float) = {
    glUseProgram(program)
    glUniform1f(uniform, x)
    checkError()
    value = (x)
  }

  override def put2F(x: Float, y: Float) = {
    glUseProgram(program)
    glUniform2f(uniform, x, y)
    checkError()
    value = (x, y)
  }

  override def put3F(x: Float, y: Float, z: Float) = {
    glUseProgram(program)
    glUniform3f(uniform, x, y, z)
    checkError()
    value = (x, y, z)
  }

  override def put4F(x: Float, y: Float, z: Float, w: Float) = {
    glUseProgram(program)
    glUniform4f(uniform, x, y, z, w)
    checkError()
    value = (x, y, z, w)
  }

  override def putMat2F(value: Mat2F) = {
    value.get(JvmShaderBuffer.array)
    glUseProgram(program)
    glUniformMatrix2fv(uniform, false, JvmShaderBuffer.array)
    checkError()
    this.value = value
  }

  override def putMat3F(value: Mat3F) = {
    value.get(JvmShaderBuffer.array)
    glUseProgram(program)
    glUniformMatrix3fv(uniform, false, JvmShaderBuffer.array)
    checkError()
    this.value = value
  }

  override def putMat4F(value: Mat4F) = {
    value.get(JvmShaderBuffer.array)
    glUseProgram(program)
    glUniformMatrix4fv(uniform, false, JvmShaderBuffer.array)
    checkError()
    this.value = value
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

  private def checkError() = {
    glGetError() match {
      case GL_NO_ERROR ⇒
      case e =>
        val name = GlUtil.constantTypeIndex.getOrElse(typ, "UNKNOWN")
        val source = Log.stackTrace(new Throwable)(1).method
        val error = s"Error $e, failed to set uniform of type $name[$typ]: with $source"
        sys.error(error)
    }
  }
}
        