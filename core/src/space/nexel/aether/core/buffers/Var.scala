package space.nexel.aether.core.buffers

import space.nexel.aether.core.types.*

/**
  * Vector get/put interface.
  */
trait Var {

  def getI(): Int
  def get2I(): Vec2I
  def get3I(): Vec3I
  def get4I(): Vec4I

  def getF(): Float
  def get2F(): Vec2F
  def get3F(): Vec3F
  def get4F(): Vec4F

//  def getD(): Double
//  def get2D(): Vec2D
//  def get3D(): Vec3D
//  def get4D(): Vec4D

  def putI(x: Int): Unit
  def put2I(x: Int, y: Int): Unit
  def put3I(x: Int, y: Int, z: Int): Unit
  def put4I(x: Int, y: Int, z: Int, w: Int): Unit

  final def put2I(v: Vec2I): Unit = put2I(v.x, v.y)
  final def put3I(v: Vec3I): Unit = put3I(v.x, v.y, v.z)
  final def put4I(v: Vec4I): Unit = put4I(v.x, v.y, v.z, v.z)

  // TODO: design array interfaces
  def putIv(v: Array[Int]): Unit

  def putF(x: Float): Unit
  def put2F(x: Float, y: Float): Unit
  def put3F(x: Float, y: Float, z: Float): Unit
  def put4F(x: Float, y: Float, z: Float, w: Float): Unit

  final def put2F(v: Vec2F): Unit = put2F(v.x, v.y)
  final def put3F(v: Vec3F): Unit = put3F(v.x, v.y, v.z)
  final def put4F(v: Vec4F): Unit = put4F(v.x, v.y, v.z, v.w)

//  def putD(x: Double)
//  def put2D(x: Double, y: Double)
//  def put3D(x: Double, y: Double, z: Double)
//  def put4D(x: Double, y: Double, z: Double, w: Double)

//  final def put2D(v: Vec2D): Unit = put2D(v.x, v.y)
//  final def put3D(v: Vec3D): Unit = put3D(v.x, v.y, v.z)
//  final def put4D(v: Vec4D): Unit = put4D(v.x, v.y, v.z, v.w)

  def putMat2F(value: Mat2F): Unit
  def putMat3F(value: Mat3F): Unit
  def putMat4F(value: Mat4F): Unit

  def getMat2F(): Mat2F
  def getMat3F(): Mat3F
  def getMat4F(): Mat4F

}