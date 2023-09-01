package aether.core.types

import math.Numeric.Implicits.infixNumericOps
import math.Fractional.Implicits.infixFractionalOps
import math.Integral.Implicits.infixIntegralOps
import math.Ordering.Implicits.infixOrderingOps
import aether.core.math.VMathF
import aether.core.math.VMathD
import aether.core.math.VMath
import aether.core.math.VMathI

trait Vec4[T](using math: VMath[T], num: Numeric[T]) extends IndexedSeq[T] {
  val x: T
  val y: T
  val z: T
  val w: T

  type V <: Vec4[T]

  override val length = 4

  def apply(index: Int): T = index match {
    case 0 => x
    case 1 => y
    case 2 => z
    case 3 => w
    case i => throw IndexOutOfBoundsException(s"Index out of range: $i")
  }

  inline def isZero: Boolean = x == num.zero && y == num.zero && z == num.zero && w == num.zero

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
  // def cross(v: V): V

  // Vector Relational Functions
  def <(v: V): Boolean
  def <=(v: V): Boolean
  def >(v: V): Boolean
  def >=(v: V): Boolean
  def ==(v: V): Boolean
  def !=(v: V): Boolean

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

  val vmath = VMathI
  type T = Int
  type V = Vec4I

  inline def apply(op: T => T): V = Vec4I(op(x), op(y), op(z), op(w))
  inline def apply(v: V, op: (T, T) => T): V = Vec4I(op(x, v.x), op(y, v.y), op(z, v.z), op(w, v.w))
  inline def apply(v1: V, v2: V, op: (T, T, T) => T): V = Vec4I(op(x, v1.x, v2.x), op(y, v1.y, v2.y), op(z, v1.z, v2.z), op(w, v1.w, v2.w))
  inline def and(v: V, op: (T, T) => Boolean): Boolean = op(x, v.x) && op(y, v.y) && op(y, v.z) && op(w, v.w)

  inline def dot(v: V): T = x * v.x + y * v.y + z * v.z + w * v.w

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

  val vmath = VMathF
  type T = Float
  type V = Vec4F

  inline def apply(op: T => T): V = Vec4F(op(x), op(y), op(z), op(w))
  inline def apply(v: V, op: (T, T) => T): V = Vec4F(op(x, v.x), op(y, v.y), op(z, v.z), op(w, v.w))
  inline def apply(v1: V, v2: V, op: (T, T, T) => T): V = Vec4F(op(x, v1.x, v2.x), op(y, v1.y, v2.y), op(z, v1.z, v2.z), op(w, v1.w, v2.w))
  inline def and(v: V, op: (T, T) => Boolean): Boolean = op(x, v.x) && op(y, v.y) && op(y, v.z) && op(w, v.w)

  inline def dot(v: V): T = x * v.x + y * v.y + z * v.z + w * v.w

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
  val vmath = VMathD
  type T = Double
  type V = Vec4D

  inline def apply(op: T => T): V = Vec4D(op(x), op(y), op(z), op(w))
  inline def apply(v: V, op: (T, T) => T): V = Vec4D(op(x, v.x), op(y, v.y), op(z, v.z), op(w, v.w))
  inline def apply(v1: V, v2: V, op: (T, T, T) => T): V = Vec4D(op(x, v1.x, v2.x), op(y, v1.y, v2.y), op(z, v1.z, v2.z), op(w, v1.w, v2.w))
  inline def and(v: V, op: (T, T) => Boolean): Boolean = op(x, v.x) && op(y, v.y) && op(y, v.z) && op(w, v.w)

  inline def dot(v: V): T = x * v.x + y * v.y + z * v.z + w * v.w

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
