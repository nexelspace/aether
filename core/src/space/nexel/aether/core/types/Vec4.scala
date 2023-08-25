package space.nexel.aether.core.types

import math.Numeric.Implicits.infixNumericOps
import math.Fractional.Implicits.infixFractionalOps
import math.Integral.Implicits.infixIntegralOps
import math.Ordering.Implicits.infixOrderingOps
import space.nexel.aether.core.math.VMathF
import space.nexel.aether.core.math.VMathD
import space.nexel.aether.core.math.VMath
import space.nexel.aether.core.math.VMathI

trait Vec4[T](using math: VMath[T], num: Numeric[T]) extends IndexedSeq[T] {
  val x: T
  val y: T
  val z: T
  val w: T

  private type V = Vec4[T]

  override val length = 4

  /** Create new instance of same type */
  protected def create(x: T, y: T, z: T, w: T): V

  def apply(index: Int): T = index match {
    case 0 => x
    case 1 => y
    case 2 => z
    case 3 => w
    case i => throw IndexOutOfBoundsException(s"Index out of range: $i")
  }

  inline def isZero: Boolean = x == num.zero && y == num.zero && z == num.zero && w == num.zero

  inline def unary_- : V = create(-x, -y, -z, -w)

  inline def *(s: T): V = create(x * s, y * s, z * s, w * s)
  inline def /(s: T): V = create(math.div(x, s), math.div(y, s), math.div(z, s), math.div(w, s))
  inline def +(s: T): V = create(x + s, y + s, z + s, w + s)
  inline def -(s: T): V = create(x - s, y - s, z - s, w - s)

  inline def *(v: V) = create(x * v.x, y * v.y, z * v.z, w * v.w)
  inline def /(v: V): V = create(math.div(x, v.x), math.div(y, v.y), math.div(z, v.z), math.div(w, v.w))
  inline def +(v: V) = create(x + v.x, y + v.y, z + v.z, w + v.w)
  inline def -(v: V) = create(x - v.x, y - v.y, z - v.z, w - v.w)

  inline def ==(v: V): Boolean = x == v.x && y == v.y && z == v.z && w == v.w
  inline def !=(v: V): Boolean = x != v.x || y != v.y || z != v.z || w != v.w

// Common Functions

  inline def abs: V = create(x.abs, y.abs, z.abs, w.abs)
  inline def sign: V = create(x.sign, y.sign, z.sign, w.sign)
  inline def floor: V = create(math.floor(x), math.floor(y), math.floor(z), math.floor(w))
  inline def ceil: V = create(math.ceil(x), math.ceil(y), math.ceil(z), math.ceil(w))

  inline def mod(v: V): V = create(math.mod(x, v.x), math.mod(y, v.y), math.mod(z, v.z), math.mod(w, v.w))
  inline def min(v: V): V = create(math.min(x, v.x), math.min(y, v.y), math.min(z, v.z), math.min(w, v.w))
  inline def max(v: V): V = create(math.max(x, v.x), math.max(y, v.y), math.max(z, v.z), math.max(w, v.w))
  inline def clamp(minVal: V, maxVal: V): V = create(
    math.clamp(x, minVal.x, maxVal.x),
    math.clamp(y, minVal.y, maxVal.y),
    math.clamp(z, minVal.z, maxVal.z),
    math.clamp(w, minVal.w, maxVal.w)
  )

// Geometric Functions

  inline def normSq: T = x * x + y * y + z * z + w * w
  inline def norm: T = math.sqrt(normSq)
  inline def distanceSq(to: V): T = (to - this).normSq
  inline def distance(to: V): T = math.sqrt(distanceSq(to))
  inline def dot(v: V): T = x * v.x + y * v.y + z * v.z + w * v.w
  inline def normalize: V = if (isZero) this else this / norm

  def get(array: Array[T], index: Int) = {
    array(index) = x
    array(index + 1) = y
    array(index + 2) = z
  }

  def get(array: Array[T], index: Int, stride: Int) = {
    array(index) = x
    array(index + stride) = y
    array(index + stride * 2) = z
    array(index + stride * 3) = w
  }

  override def toString: String = s"[$x, $y, $z, $w]"

}

object Vec4I {  
  given Conversion[Vec4[Int], Vec4I] = (v: Vec4[Int]) => v

  val Zero = Vec4I(0)
  val One = Vec4I(1)
  def apply(v: (Int, Int, Int, Int)) = new Vec4I(v._1, v._2, v._3, v._4)
  def apply(v: Int): Vec4I = new Vec4I(v, v, v, v)
  def apply(a: Array[Int], index: Int = 0) =
    new Vec4I(a(index + 0), a(index + 1), a(index + 2), a(index + 3))
}

case class Vec4I(x: Int, y: Int, z: Int, w: Int) extends Vec4[Int] {
  inline def create(x: Int, y: Int, z: Int, w: Int) = Vec4I(x, y, z, w)

  def &(c: Int): Vec4I = new Vec4I(x & c, y & c, z & c, w & c)
  def |(c: Int): Vec4I = new Vec4I(x | c, y | c, z | c, w | c)
  def >>(c: Int): Vec4I = new Vec4I(x >> c, y >> c, z >> c, w >> c)
  def <<(c: Int): Vec4I = new Vec4I(x << c, y << c, z << c, w << c)
}

object Vec4F {  
  given Conversion[Vec4[Float], Vec4F] = (v: Vec4[Float]) => v

  val Zero = Vec4F(0)
  val One = Vec4F(1)
  def apply(v: (Float, Float, Float, Float)) = new Vec4F(v._1, v._2, v._3, v._4)
  def apply(v: Float): Vec4F = new Vec4F(v, v, v, v)
  def apply(a: Array[Float], index: Int = 0) =
    new Vec4F(a(index + 0), a(index + 1), a(index + 2), a(index + 3))
}

case class Vec4F(x: Float, y: Float, z: Float, w: Float) extends Vec4[Float] {
  inline def create(x: Float, y: Float, z: Float, w: Float) = Vec4F(x, y, z, w)
}

object Vec4D {  
  given Conversion[Vec4[Double], Vec4D] = (v: Vec4[Double]) => v

  val Zero = Vec4D(0)
  val One = Vec4D(1)
  def apply(v: (Double, Double, Double, Double)) = new Vec4D(v._1, v._2, v._3, v._4)
  def apply(v: Double): Vec4D = Vec4D(v, v, v, v)
  def apply(a: Array[Double], index: Int = 0) =
    new Vec4D(a(index + 0), a(index + 1), a(index + 2), a(index + 3))
}

case class Vec4D(x: Double, y: Double, z: Double, w: Double) extends Vec4[Double] {
  inline def create(x: Double, y: Double, z: Double, w: Double) = Vec4D(x, y, z, w)
}
