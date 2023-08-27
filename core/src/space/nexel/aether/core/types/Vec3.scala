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

  type V <: Vec3[T]

  override val length = 3

  def apply(index: Int): T = index match {
    case 0 => x
    case 1 => y
    case 2 => z
    case i => throw IndexOutOfBoundsException(s"Index out of range: $i")
  }

  inline def isZero: Boolean = x == num.zero && y == num.zero && z == num.zero
  inline def minVal: T = reduce(math.min(_, _))
  inline def maxVal: T = reduce(math.max(_, _))

  // def ++ : V
  // def -- : V
  def unary_- : V
  // def ! : Vec2[Boolean]

  def *(s: T): V
  def /(s: T): V
  def +(s: T): V
  def -(s: T): V

  def *(v: V): V
  def /(v: V): V
  def +(v: V): V
  def -(v: V): V

  // Common Functions

  def abs: V
  def sign: V
  def floor: V
  def ceil: V

  def mod(v: V): V
  def min(v: V): V
  def max(v: V): V
  def clamp(minVal: V, maxVal: V): V
  def mix(v: V, a: V): V
  def step(edge: V): V
  def step0: V
  def smoothstep(edge0: V, edge1: V): V

  def mod(s: T): V
  def min(s: T): V
  def max(s: T): V
  def clamp(minVal: T, maxVal: T): V
  def mix(v: V, a: T): V
  def step(edge: T): V
  def smoothstep(edge0: T, edge1: T): V

  // Geometric Functions
  def normSq: T
  def norm: T
  def normalize: V
  def distanceSq(to: V): T
  def distance(to: V): T
  def dot(v: V): T
  def cross(v: V): V

  // Vector Relational Functions
  def <(v: V): Boolean
  def <=(v: V): Boolean
  def >(v: V): Boolean
  def >=(v: V): Boolean
  def ==(v: V): Boolean
  def !=(v: V): Boolean

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
  val Zero = Vec3I(0)
  val One = Vec3I(1)
  def apply(v: (Int, Int, Int)) = new Vec3I(v._1, v._2, v._3)
  def apply(v: Int): Vec3I = new Vec3I(v, v, v)
  def apply(a: Array[Int], index: Int = 0) =
    new Vec3I(a(index + 0), a(index + 1), a(index + 2))
}

case class Vec3I(x: Int, y: Int, z: Int) extends Vec3[Int] {

  val vmath = VMathI
  type T = Int
  type V = Vec3I

  inline def apply(op: T => T): V = Vec3I(op(x), op(y), op(z))
  inline def apply(v: V, op: (T, T) => T): V = Vec3I(op(x, v.x), op(y, v.y), op(z, v.z))
  inline def apply(v1: V, v2: V, op: (T, T, T) => T): V = Vec3I(op(x, v1.x, v2.x), op(y, v1.y, v2.y), op(z, v1.z, v2.z))
  inline def and(v: V, op: (T, T) => Boolean): Boolean = op(x, v.x) && op(y, v.y) && op(y, v.z)

  inline def dot(v: V): T = x * v.x + y * v.y + z * v.z
  inline def cross(v: V): V = Vec3I(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x)

  // -- Integral functions
  inline def &(c: T): V = apply(t => t & c)
  inline def |(c: T): V = apply(t => t | c)
  inline def >>(c: T): V = apply(t => t >> c)
  inline def <<(c: T): V = apply(t => t << c)

  // Definitions independent of T and length

  inline def unary_- : V = apply(t => -t)
  inline def *(s: T): V = apply(t => t * s)
  inline def /(s: T): V = apply(t => t / s)
  inline def +(s: T): V = apply(t => t + s)
  inline def -(s: T): V = apply(t => t - s)
  inline def *(v: V): V = apply(v, (a, b) => a * b)
  inline def /(v: V): V = apply(v, (a, b) => a / b)
  inline def +(v: V): V = apply(v, (a, b) => a + b)
  inline def -(v: V): V = apply(v, (a, b) => a - b)
  inline def ==(v: V): Boolean = and(v, (a, b) => a == b)
  inline def !=(v: V): Boolean = and(v, (a, b) => a != b)
  inline def <(v: V): Boolean = and(v, (a, b) => a < b)
  inline def >(v: V): Boolean = and(v, (a, b) => a > b)
  inline def <=(v: V): Boolean = and(v, (a, b) => a <= b)
  inline def >=(v: V): Boolean = and(v, (a, b) => a >= b)

  // -- Common Functions
  inline def abs: V = apply(vmath.abs(_))
  inline def sign: V = apply(vmath.sign(_))
  inline def floor: V = apply(vmath.floor(_))
  inline def ceil: V = apply(vmath.ceil(_))
  inline def mod(v: V): V = apply(v, vmath.mod(_, _))
  inline def min(v: V): V = apply(v, vmath.min(_, _))
  inline def max(v: V): V = apply(v, vmath.max(_, _))
  inline def clamp(minVal: V, maxVal: V): V = apply(minVal, maxVal, vmath.clamp(_, _, _))
  inline def mix(v: V, a: V): V = apply(v, a, vmath.mix(_, _, _))
  inline def step(edge: V): V = apply(edge, vmath.step(_, _))
  inline def smoothstep(edge0: V, edge1: V): V = apply(edge0, edge1, vmath.smoothstep(_, _, _))
  inline def mod(s: T): V = apply(vmath.mod(_, s))
  inline def min(s: T): V = apply(vmath.min(_, s))
  inline def max(s: T): V = apply(vmath.max(_, s))
  inline def clamp(minVal: T, maxVal: T): V = apply(vmath.clamp(_, minVal, maxVal))
  inline def mix(v: V, a: T): V = apply(v, vmath.mix(_, _, a))
  inline def step(edge: T): V = apply(vmath.step(_, edge))
  inline def step0: V = apply(vmath.step(_, 0))
  inline def smoothstep(edge0: T, edge1: T): V = apply(vmath.smoothstep(_, edge0, edge1))

  // -- Geometric Functions
  inline def normSq: T = dot(this)
  inline def norm: T = vmath.sqrt(normSq)
  inline def normalize: V = if (isZero) this else this / norm
  inline def distanceSq(to: V): T = (to - this).normSq
  inline def distance(to: V): T = vmath.sqrt(distanceSq(to))

}

object Vec3F {
  val Zero = Vec3F(0)
  val One = Vec3F(1)
  def apply(v: (Float, Float, Float)) = new Vec3F(v._1, v._2, v._3)
  def apply(v: Float): Vec3F = new Vec3F(v, v, v)
  def apply(a: Array[Float], index: Int = 0) =
    new Vec3F(a(index + 0), a(index + 1), a(index + 2))
}

case class Vec3F(x: Float, y: Float, z: Float) extends Vec3[Float] {
  val vmath = VMathF
  type T = Float
  type V = Vec3F

  inline def apply(op: T => T): V = Vec3F(op(x), op(y), op(z))
  inline def apply(v: V, op: (T, T) => T): V = Vec3F(op(x, v.x), op(y, v.y), op(z, v.z))
  inline def apply(v1: V, v2: V, op: (T, T, T) => T): V = Vec3F(op(x, v1.x, v2.x), op(y, v1.y, v2.y), op(z, v1.z, v2.z))
  inline def and(v: V, op: (T, T) => Boolean): Boolean = op(x, v.x) && op(y, v.y) && op(y, v.z)

  inline def dot(v: V): T = x * v.x + y * v.y + z * v.z
  inline def cross(v: V): V = Vec3F(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x)

  // Definitions independent of T and length

  inline def unary_- : V = apply(t => -t)
  inline def *(s: T): V = apply(t => t * s)
  inline def /(s: T): V = apply(t => t / s)
  inline def +(s: T): V = apply(t => t + s)
  inline def -(s: T): V = apply(t => t - s)
  inline def *(v: V): V = apply(v, (a, b) => a * b)
  inline def /(v: V): V = apply(v, (a, b) => a / b)
  inline def +(v: V): V = apply(v, (a, b) => a + b)
  inline def -(v: V): V = apply(v, (a, b) => a - b)
  inline def ==(v: V): Boolean = and(v, (a, b) => a == b)
  inline def !=(v: V): Boolean = and(v, (a, b) => a != b)
  inline def <(v: V): Boolean = and(v, (a, b) => a < b)
  inline def >(v: V): Boolean = and(v, (a, b) => a > b)
  inline def <=(v: V): Boolean = and(v, (a, b) => a <= b)
  inline def >=(v: V): Boolean = and(v, (a, b) => a >= b)

  // -- Common Functions
  inline def abs: V = apply(vmath.abs(_))
  inline def sign: V = apply(vmath.sign(_))
  inline def floor: V = apply(vmath.floor(_))
  inline def ceil: V = apply(vmath.ceil(_))
  inline def mod(v: V): V = apply(v, vmath.mod(_, _))
  inline def min(v: V): V = apply(v, vmath.min(_, _))
  inline def max(v: V): V = apply(v, vmath.max(_, _))
  inline def clamp(minVal: V, maxVal: V): V = apply(minVal, maxVal, vmath.clamp(_, _, _))
  inline def mix(v: V, a: V): V = apply(v, a, vmath.mix(_, _, _))
  inline def step(edge: V): V = apply(edge, vmath.step(_, _))
  inline def smoothstep(edge0: V, edge1: V): V = apply(edge0, edge1, vmath.smoothstep(_, _, _))
  inline def mod(s: T): V = apply(vmath.mod(_, s))
  inline def min(s: T): V = apply(vmath.min(_, s))
  inline def max(s: T): V = apply(vmath.max(_, s))
  inline def clamp(minVal: T, maxVal: T): V = apply(vmath.clamp(_, minVal, maxVal))
  inline def mix(v: V, a: T): V = apply(v, vmath.mix(_, _, a))
  inline def step(edge: T): V = apply(vmath.step(_, edge))
  inline def step0: V = apply(vmath.step(_, 0))
  inline def smoothstep(edge0: T, edge1: T): V = apply(vmath.smoothstep(_, edge0, edge1))

  // -- Geometric Functions
  inline def normSq: T = dot(this)
  inline def norm: T = vmath.sqrt(normSq)
  inline def normalize: V = if (isZero) this else this / norm
  inline def distanceSq(to: V): T = (to - this).normSq
  inline def distance(to: V): T = vmath.sqrt(distanceSq(to))
}
object Vec3D {
  val Zero = Vec3D(0)
  val One = Vec3D(1)
  def apply(v: (Double, Double, Double)) = new Vec3D(v._1, v._2, v._3)
  def apply(v: Double): Vec3D = new Vec3D(v, v, v)
  def apply(a: Array[Double], index: Int = 0) =
    new Vec3D(a(index + 0), a(index + 1), a(index + 2))
}

case class Vec3D(x: Double, y: Double, z: Double) extends Vec3[Double] {
  val vmath = VMathD
  type T = Double
  type V = Vec3D

  inline def apply(op: T => T): V = Vec3D(op(x), op(y), op(z))
  inline def apply(v: V, op: (T, T) => T): V = Vec3D(op(x, v.x), op(y, v.y), op(z, v.z))
  inline def apply(v1: V, v2: V, op: (T, T, T) => T): V = Vec3D(op(x, v1.x, v2.x), op(y, v1.y, v2.y), op(z, v1.z, v2.z))
  inline def and(v: V, op: (T, T) => Boolean): Boolean = op(x, v.x) && op(y, v.y) && op(y, v.z)

  inline def dot(v: V): T = x * v.x + y * v.y + z * v.z
  inline def cross(v: V): V = Vec3D(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x)

  // Definitions independent of T and length

  inline def unary_- : V = apply(t => -t)
  inline def *(s: T): V = apply(t => t * s)
  inline def /(s: T): V = apply(t => t / s)
  inline def +(s: T): V = apply(t => t + s)
  inline def -(s: T): V = apply(t => t - s)
  inline def *(v: V): V = apply(v, (a, b) => a * b)
  inline def /(v: V): V = apply(v, (a, b) => a / b)
  inline def +(v: V): V = apply(v, (a, b) => a + b)
  inline def -(v: V): V = apply(v, (a, b) => a - b)
  inline def ==(v: V): Boolean = and(v, (a, b) => a == b)
  inline def !=(v: V): Boolean = and(v, (a, b) => a != b)
  inline def <(v: V): Boolean = and(v, (a, b) => a < b)
  inline def >(v: V): Boolean = and(v, (a, b) => a > b)
  inline def <=(v: V): Boolean = and(v, (a, b) => a <= b)
  inline def >=(v: V): Boolean = and(v, (a, b) => a >= b)

  // -- Common Functions
  inline def abs: V = apply(vmath.abs(_))
  inline def sign: V = apply(vmath.sign(_))
  inline def floor: V = apply(vmath.floor(_))
  inline def ceil: V = apply(vmath.ceil(_))
  inline def mod(v: V): V = apply(v, vmath.mod(_, _))
  inline def min(v: V): V = apply(v, vmath.min(_, _))
  inline def max(v: V): V = apply(v, vmath.max(_, _))
  inline def clamp(minVal: V, maxVal: V): V = apply(minVal, maxVal, vmath.clamp(_, _, _))
  inline def mix(v: V, a: V): V = apply(v, a, vmath.mix(_, _, _))
  inline def step(edge: V): V = apply(edge, vmath.step(_, _))
  inline def step0: V = apply(vmath.step(_, 0))
  inline def smoothstep(edge0: V, edge1: V): V = apply(edge0, edge1, vmath.smoothstep(_, _, _))
  inline def mod(s: T): V = apply(vmath.mod(_, s))
  inline def min(s: T): V = apply(vmath.min(_, s))
  inline def max(s: T): V = apply(vmath.max(_, s))
  inline def clamp(minVal: T, maxVal: T): V = apply(vmath.clamp(_, minVal, maxVal))
  inline def mix(v: V, a: T): V = apply(v, vmath.mix(_, _, a))
  inline def step(edge: T): V = apply(vmath.step(_, edge))
  inline def smoothstep(edge0: T, edge1: T): V = apply(vmath.smoothstep(_, edge0, edge1))

  // -- Geometric Functions
  inline def normSq: T = dot(this)
  inline def norm: T = vmath.sqrt(normSq)
  inline def normalize: V = if (isZero) this else this / norm
  inline def distanceSq(to: V): T = (to - this).normSq
  inline def distance(to: V): T = vmath.sqrt(distanceSq(to))

}
