package space.nexel.aether.lib.canvas.shader

import space.nexel.aether.core.graphics.*
import space.nexel.aether.core.buffers.*
import space.nexel.aether.core.types.*
import ShaderBuffer.ShaderBufferFactory
import space.nexel.aether.core.platform.Platform

object ShaderVarBuffer {

  /**
    * @param flags
    * @param capacity Number of elements in buffer.
    * @param numComponents Number of components in one buffer element.
    * @return
    */
  def apply(flags: Int, capacity: Int, numComponents: Int)(using platform: Platform): ShaderVarBuffer = {
    new ShaderVarBuffer(ShaderBuffer(flags, capacity * numComponents), numComponents)
  }
  //TODO: new ShaderBuffer Config
//  def apply(target: Target, dataType: DataType, flags: Int, capacity: Int, numComponents: Int): ShaderVarBuffer = {
//    new ShaderVarBuffer(ShaderBuffer.create(target, dataType, flags, capacity), numComponents)
//  }

}

/**
  * Shader buffer with extended datatype support (Var).
  */
class ShaderVarBuffer(val buffer: ShaderBuffer, val numComponents: Int) extends ElementBuffer with Var /*with BufferWrapper*/ {
  assert(numComponents > 0 && numComponents <= 4, s"Unsupported number of components: $numComponents")

  private val b: Buffer = buffer
  export b._

  val dataType = buffer.dataType
  def size: Int = buffer.size / numComponents
  assert(size * numComponents == buffer.size, s"$numComponents, ${buffer.size}, $size")

  override def toString = s"$numComponents:"+buffer.toString

  def elementPos: Int = {
    val p = buffer.position / numComponents
    assert(p * numComponents == buffer.position, s"Invalid element position: ${buffer.position} / $numComponents == $p")
    p
  }

//  def getI(): Int = buffer.getI()
//  def getFloat(): Float = buffer.getFloat()
//  def putInt(value: Int): Unit = buffer.putInt(value)
//  def putFloat(value: Float): Unit = buffer.putFloat(value)
  def resizeBuffer(size: Int): Unit = {
    assert(buffer.config.dynamic)
    buffer.resizeBuffer(size)
  }

  private def ensureSize(count: Int) = {
    if (remaining < count) {
      //Log("Expand buffer " + capacity + " -> " + (capacity * 2))
      resizeBuffer(capacity * 2)
      assert(remaining >= count)
    }
  }

  // ---- Var

  override def getI(): Int = {
    val v = buffer.getI()
    position = position - 1 + numComponents
    v
  }

  override def get2I(): Vec2I = {
    assert(numComponents >= 2)
    val v = Vec2I(buffer.getI(), buffer.getI())
    position = position - 2 + numComponents
    v
  }

  override def get3I(): Vec3I = {
    assert(numComponents >= 3)
    val v = Vec3I(buffer.getI(), buffer.getI(), buffer.getI())
    position = position - 3 + numComponents
    v
  }

  override def get4I(): Vec4I = {
    assert(numComponents == 4)
    Vec4I(buffer.getI(), buffer.getI(), buffer.getI(), buffer.getI())
  }

  override def getF(): Float = {
    val v = buffer.getF()
    position = position - 1 + numComponents
    v
  }

  override def get2F(): Vec2F = {
    assert(numComponents >= 2)
    val v = Vec2F(buffer.getF(), buffer.getF())
    position = position - 2 + numComponents
    v
  }

  override def get3F(): Vec3F = {
    assert(numComponents >= 3)
    val v = Vec3F(buffer.getF(), buffer.getF(), buffer.getF())
    position = position - 3 + numComponents
    v
  }

  override def get4F(): Vec4F = {
    assert(numComponents == 4)
    Vec4F(buffer.getF(), buffer.getF(), buffer.getF(), buffer.getF())
  }

  override def getD(): Double = {
    val v = buffer.getD()
    position = position - 1 + numComponents
    v
  }

  def putI(x: Int) = put4I(x, 0, 0, 0)
  def put2I(x: Int, y: Int) = put4I(x, y, 0, 0)
  def put3I(x: Int, y: Int, z: Int) = put4I(x, y, z, 0)

  override def put4I(x: Int, y: Int, z: Int, w: Int) = {
    ensureSize(numComponents)
    numComponents match {
      case 1 =>
        buffer.putI(x)
      case 2 =>
        buffer.putI(x)
        buffer.putI(y)
      case 3 =>
        buffer.putI(x)
        buffer.putI(y)
        buffer.putI(z)
      case 4 =>
        buffer.putI(x)
        buffer.putI(y)
        buffer.putI(z)
        buffer.putI(w)
      case _ => ???
    }
  }

  def putIv(v: Array[Int]) = v.foreach(putI)

  def putF(x: Float) = put4F(x, 0, 0, 0)
  def put2F(x: Float, y: Float) = put4F(x, y, 0, 0)
  def put3F(x: Float, y: Float, z: Float) = put4F(x, y, z, 0)

  override def put4F(x: Float, y: Float, z: Float, w: Float) = {
    ensureSize(numComponents)
    numComponents match {
      case 1 =>
        buffer.putF(x)
      case 2 =>
        buffer.putF(x)
        buffer.putF(y)
      case 3 =>
        buffer.putF(x)
        buffer.putF(y)
        buffer.putF(z)
      case 4 =>
        buffer.putF(x)
        buffer.putF(y)
        buffer.putF(z)
        buffer.putF(w)
      case _ => ???
    }
  }

  def putD(x: Double) = put4F(x.toFloat, 0, 0, 0)

  def putMat2F(value: Mat2F) = ???
  def putMat3F(value: Mat3F) = ???
  def putMat4F(value: Mat4F) = ???

  def getMat2F() = ???
  def getMat3F() = ???
  def getMat4F() = ???

}
