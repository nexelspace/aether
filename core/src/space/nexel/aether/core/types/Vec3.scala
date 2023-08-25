package space.nexel.aether.core.types

import math.Numeric.Implicits.infixNumericOps
import math.Fractional.Implicits.infixFractionalOps
import math.Integral.Implicits.infixIntegralOps
import math.Ordering.Implicits.infixOrderingOps
import space.nexel.aether.core.math.VMathF
import space.nexel.aether.core.math.VMathD
import space.nexel.aether.core.math.VMath
import space.nexel.aether.core.math.VMathI

trait Vec3[T](using math: VMath[T], num: Numeric[T]) extends IndexedSeq[T] {
  val x: T
  val y: T
  val z: T

  private type V = Vec3[T]

  override val length = 3

  /** Create new instance of same type */
  protected def create(x: T, y: T, z: T): V
  protected def step0: V

  def apply(index: Int): T = index match {
    case 0 => x
    case 1 => y
    case 2 => z
    case i => throw IndexOutOfBoundsException(s"Index out of range: $i")
  }

  inline def isZero: Boolean = x == num.zero && y == num.zero && z == num.zero

  inline def unary_- : V = create(-x, -y, -z)

  inline def *(s: T): V = create(x * s, y * s, z * s)
  inline def /(s: T): V = create(math.div(x, s), math.div(y, s), math.div(z, s))
  inline def +(s: T): V = create(x + s, y + s, z + s)
  inline def -(s: T): V = create(x - s, y - s, z - s)

  inline def *(v: V) = create(x * v.x, y * v.y, z * v.z)
  inline def /(v: V): V = create(math.div(x, v.x), math.div(y, v.y), math.div(z, v.z))
  inline def +(v: V) = create(x + v.x, y + v.y, z + v.z)
  inline def -(v: V) = create(x - v.x, y - v.y, z - v.z)
  inline def ==(v: V): Boolean = x == v.x && y == v.y && z == v.z
  inline def !=(v: V): Boolean = x != v.x || y != v.y || z != v.z
  inline def <(v: V) = x < v.x && y < v.y && z < v.z
  inline def >(v: V) = x > v.x && y > v.y && z > v.z
  inline def <=(v: V) = x <= v.x && y <= v.y && z <= v.z
  inline def >=(v: V) = x >= v.x && y >= v.y && z >= v.z

  // Common Functions

  inline def abs: V = create(x.abs, y.abs, z.abs)
  inline def sign: V = create(x.sign, y.sign, z.sign)
  inline def floor: V = create(math.floor(x), math.floor(y), math.floor(z))
  inline def ceil: V = create(math.ceil(x), math.ceil(y), math.ceil(z))

  inline def mod(v: V): V = create(math.mod(x, v.x), math.mod(y, v.y), math.mod(z, v.z))
  inline def min(v: V): V = create(math.min(x, v.x), math.min(y, v.y), math.min(z, v.z))
  inline def maxValue: T = math.max(x, y, z)
  inline def max(v: V): V = create(math.max(x, v.x), math.max(y, v.y), math.max(z, v.z))
  inline def clamp(minVal: V, maxVal: V): V = create(math.clamp(x, minVal.x, maxVal.x), math.clamp(y, minVal.y, maxVal.y), math.clamp(z, minVal.z, maxVal.z))
  inline def mix(v: V, a: V): V = create(math.mix(x, v.x, a.x), math.mix(y, v.y, a.y), math.mix(z, v.z, a.z))
  inline def step(edge: V): V = create(math.step(x, edge.x), math.step(y, edge.y), math.step(z, edge.z))
  inline def smoothstep(edge0: V, edge1: V): V = create(math.smoothstep(edge0.x, edge1.x, x), math.smoothstep(edge0.y, edge1.y, y), math.smoothstep(edge0.z, edge1.z, z))

  inline def mod(s: T): V = mod(create(s, s, s))
  inline def min(s: T): V = min(create(s, s, s))
  inline def max(s: T): V = max(create(s, s, s))
  inline def clamp(minVal: T, maxVal: T): V = create(math.clamp(x, minVal, maxVal), math.clamp(y, minVal, maxVal), math.clamp(z, minVal, maxVal))
  inline def mix(v: V, a: T): V = create(math.mix(x, v.x, a), math.mix(y, v.y, a), math.mix(z, v.z, a))
  inline def step(edge: T): V = create(math.step(x, edge), math.step(y, edge), math.step(z, edge))
  inline def smoothstep(edge0: T, edge1: T): V = create(math.smoothstep(edge0, edge1, x), math.smoothstep(edge0, edge1, y), math.smoothstep(edge0, edge1, z))

  // Geometric Functions

  inline def normSq: T = x * x + y * y + z * z
  inline def norm: T = math.sqrt(normSq)
  inline def distanceSq(to: V): T = (to - this).normSq
  inline def distance(to: V): T = math.sqrt(distanceSq(to))
  inline def dot(v: V): T = x * v.x + y * v.y + z * v.z
  inline def cross(v: V): V = create(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x) 
  inline def normalize: V = if (isZero) this else this / norm

  // Vector Relational Functions
  //TODO

  // --

  def get(array: Array[T], index: Int) = {
    array(index) = x
    array(index + 1) = y
    array(index + 2) = z
  }

  def get(array: Array[T], index: Int, stride: Int) = {
    array(index) = x
    array(index + stride) = y
    array(index + stride * 2) = z
  }

  override def toString: String = s"[$x, $y, $z]"

}

// ---- Vec3 Types ----

object Vec3I {  
  given c: Conversion[Vec3[Int], Vec3I] = (v: Vec3[Int]) => v

  val Zero = Vec3I(0)
  val One = Vec3I(1)
  def apply(v: (Int, Int, Int)) = new Vec3I(v._1, v._2, v._3)
  def apply(v: Int): Vec3I = new Vec3I(v, v, v)
  def apply(a: Array[Int], index: Int = 0) =
    new Vec3I(a(index + 0), a(index + 1), a(index + 2))
}

case class Vec3I(x: Int, y: Int, z: Int) extends Vec3[Int] {
  inline def create(x: Int, y: Int, z: Int) = Vec3I(x, y, z)
  inline def step0 = Vec3I(if (x < 0) 0 else 1, if (y < 0) 0 else 1, if (z < 0) 0 else 1)

  def &(c: Int): Vec3I = new Vec3I(x & c, y & c, z & c)
  def |(c: Int): Vec3I = new Vec3I(x | c, y | c, z | c)
  def >>(c: Int): Vec3I = new Vec3I(x >> c, y >> c, z >> c)
  def <<(c: Int): Vec3I = new Vec3I(x << c, y << c, z << c)
}

object Vec3F {  
  given Conversion[Vec3[Float], Vec3F] = (v: Vec3[Float]) => v

  val Zero = Vec3F(0)
  val One = Vec3F(1)
  def apply(v: (Float, Float, Float)) = new Vec3F(v._1, v._2, v._3)
  def apply(v: Float): Vec3F = new Vec3F(v, v, v)
  def apply(a: Array[Float], index: Int = 0) =
    new Vec3F(a(index + 0), a(index + 1), a(index + 2))
}

case class Vec3F(x: Float, y: Float, z: Float) extends Vec3[Float] {
  inline def create(x: Float, y: Float, z: Float) = Vec3F(x, y, z)
  inline def step0 = Vec3F(if (x < 0) 0 else 1, if (y < 0) 0 else 1, if (z < 0) 0 else 1)
}
object Vec3D {  
  given Conversion[Vec3[Double], Vec3D] = (v: Vec3[Double]) => v

  val Zero = Vec3D(0)
  val One = Vec3D(1)
  def apply(v: (Double, Double, Double)) = new Vec3D(v._1, v._2, v._3)
  def apply(v: Double): Vec3D = new Vec3D(v, v, v)
  def apply(a: Array[Double], index: Int = 0) =
    new Vec3D(a(index + 0), a(index + 1), a(index + 2))}

case class Vec3D(x: Double, y: Double, z: Double) extends Vec3[Double] {
  inline def create(x: Double, y: Double, z: Double) = Vec3D(x, y, z)
  inline def step0 = Vec3D(if (x < 0) 0 else 1, if (y < 0) 0 else 1, if (z < 0) 0 else 1)
}

