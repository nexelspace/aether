package aether.core.types

import math.Numeric.Implicits.infixNumericOps
import math.Fractional.Implicits.infixFractionalOps
import math.Integral.Implicits.infixIntegralOps
import math.Ordering.Implicits.infixOrderingOps
import aether.core.math.MathF
import aether.core.math.MathD
import aether.core.math.MathBase
import aether.core.math.MathI
import scala.annotation.targetName

trait Vec2[T](using Math: MathBase[T], num: Numeric[T]) extends IndexedSeq[T] {
  val x: T
  val y: T

  type V <: Vec2[T]

  override val length = 2

  def apply(index: Int): T = index match {
    case 0 => x
    case 1 => y
    case i => throw IndexOutOfBoundsException(s"Index out of range: $i")
  }

  inline def isZero: Boolean = x == num.zero && y == num.zero

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
  def cross(v: V): T

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
  }

  def get(array: Array[T], index: Int, stride: Int) = {
    array(index) = x
    array(index + stride) = y
  }

  def toVec2F = Vec2F(x.toFloat, y.toFloat)

  override def toString: String = s"[$x, $y]"

}

object Vec2I {
  val Zero = Vec2I(0)
  val One = Vec2I(1)
  def apply(v: (Int, Int)) = new Vec2I(v._1, v._2)
  def apply(v: Int): Vec2I = new Vec2I(v, v)
  def apply(a: Array[Int], index: Int = 0) =
    new Vec2I(a(index + 0), a(index + 1))

}

case class Vec2I(x: Int, y: Int) extends Vec2[Int] {

  val Math = MathI
  type T = Int
  type V = Vec2I

  inline def apply(op: T => T): V = Vec2I(op(x), op(y))
  inline def apply(v: V, op: (T, T) => T): V = Vec2I(op(x, v.x), op(y, v.y))
  inline def apply(v1: V, v2: V, op: (T, T, T) => T): V = Vec2I(op(x, v1.x, v2.x), op(y, v1.y, v2.y))
  inline def and(v: V, op: (T, T) => Boolean): Boolean = op(x, v.x) && op(y, v.y)

  inline def dot(v: V): T = x * v.x + y * v.y
  inline def cross(v: V): T = (x * v.y) - (y * v.x)

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
  inline def abs: V = apply(Math.abs(_))
  inline def sign: V = apply(Math.sign(_))
  inline def floor: V = apply(Math.floor(_))
  inline def ceil: V = apply(Math.ceil(_))
  inline def mod(v: V): V = apply(v, Math.mod(_, _))
  inline def min(v: V): V = apply(v, Math.min(_, _))
  inline def max(v: V): V = apply(v, Math.max(_, _))
  inline def clamp(minVal: V, maxVal: V): V = apply(minVal, maxVal, Math.clamp(_, _, _))
  inline def mix(v: V, a: V): V = apply(v, a, Math.mix(_, _, _))
  inline def step(edge: V): V = apply(edge, Math.step(_, _))
  inline def smoothstep(edge0: V, edge1: V): V = apply(edge0, edge1, Math.smoothstep(_, _, _))
  inline def mod(s: T): V = apply(Math.mod(_, s))
  inline def min(s: T): V = apply(Math.min(_, s))
  inline def max(s: T): V = apply(Math.max(_, s))
  inline def clamp(minVal: T, maxVal: T): V = apply(Math.clamp(_, minVal, maxVal))
  inline def mix(v: V, a: T): V = apply(v, Math.mix(_, _, a))
  inline def step(edge: T): V = apply(Math.step(_, edge))
  inline def smoothstep(edge0: T, edge1: T): V = apply(Math.smoothstep(_, edge0, edge1))

  // -- Geometric Functions
  inline def normSq: T = dot(this)
  inline def norm: T = Math.sqrt(normSq)
  inline def normalize: V = if (isZero) this else this / norm
  inline def distanceSq(to: V): T = (to - this).normSq
  inline def distance(to: V): T = Math.sqrt(distanceSq(to))

}

object Vec2F {
  val Zero = Vec2F(0)
  val One = Vec2F(1)
  def apply(v: (Float, Float)) = new Vec2F(v._1, v._2)
  def apply(v: Float) = new Vec2F(v, v)
  def apply(a: Array[Float], index: Int = 0) =
    new Vec2F(a(index + 0), a(index + 1))
}

case class Vec2F(x: Float, y: Float) extends Vec2[Float] {
  val Math = MathF
  type T = Float
  type V = Vec2F

  inline def apply(op: T => T): V = Vec2F(op(x), op(y))
  inline def apply(v: V, op: (T, T) => T): V = Vec2F(op(x, v.x), op(y, v.y))
  inline def apply(v1: V, v2: V, op: (T, T, T) => T): V = Vec2F(op(x, v1.x, v2.x), op(y, v1.y, v2.y))
  inline def and(v: V, op: (T, T) => Boolean): Boolean = op(x, v.x) && op(y, v.y)

  inline def dot(v: V): T = x * v.x + y * v.y
  inline def cross(v: V): T = (x * v.y) - (y * v.x)

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
  inline def abs: V = apply(Math.abs(_))
  inline def sign: V = apply(Math.sign(_))
  inline def floor: V = apply(Math.floor(_))
  inline def ceil: V = apply(Math.ceil(_))
  inline def mod(v: V): V = apply(v, Math.mod(_, _))
  inline def min(v: V): V = apply(v, Math.min(_, _))
  inline def max(v: V): V = apply(v, Math.max(_, _))
  inline def clamp(minVal: V, maxVal: V): V = apply(minVal, maxVal, Math.clamp(_, _, _))
  inline def mix(v: V, a: V): V = apply(v, a, Math.mix(_, _, _))
  inline def step(edge: V): V = apply(edge, Math.step(_, _))
  inline def smoothstep(edge0: V, edge1: V): V = apply(edge0, edge1, Math.smoothstep(_, _, _))
  inline def mod(s: T): V = apply(Math.mod(_, s))
  inline def min(s: T): V = apply(Math.min(_, s))
  inline def max(s: T): V = apply(Math.max(_, s))
  inline def clamp(minVal: T, maxVal: T): V = apply(Math.clamp(_, minVal, maxVal))
  inline def mix(v: V, a: T): V = apply(v, Math.mix(_, _, a))
  inline def step(edge: T): V = apply(Math.step(_, edge))
  inline def smoothstep(edge0: T, edge1: T): V = apply(Math.smoothstep(_, edge0, edge1))

  // -- Geometric Functions
  inline def normSq: T = dot(this)
  inline def norm: T = Math.sqrt(normSq)
  inline def normalize: V = if (isZero) this else this / norm
  inline def distanceSq(to: V): T = (to - this).normSq
  inline def distance(to: V): T = Math.sqrt(distanceSq(to))
}

object Vec2D {
  val Zero = Vec2D(0)
  val One = Vec2D(1)
  def apply(v: (Double, Double)) = new Vec2D(v._1, v._2)
  def apply(v: Double) = new Vec2D(v, v)
  def apply(a: Array[Double], index: Int = 0) =
    new Vec2D(a(index + 0), a(index + 1))
}

case class Vec2D(x: Double, y: Double) extends Vec2[Double] {
  val Math = MathD
  type T = Double
  type V = Vec2D

  inline def apply(op: T => T): V = Vec2D(op(x), op(y))
  inline def apply(v: V, op: (T, T) => T): V = Vec2D(op(x, v.x), op(y, v.y))
  inline def apply(v1: V, v2: V, op: (T, T, T) => T): V = Vec2D(op(x, v1.x, v2.x), op(y, v1.y, v2.y))
  inline def and(v: V, op: (T, T) => Boolean): Boolean = op(x, v.x) && op(y, v.y)

  inline def dot(v: V): T = x * v.x + y * v.y
  inline def cross(v: V): T = (x * v.y) - (y * v.x)

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
  inline def abs: V = apply(Math.abs(_))
  inline def sign: V = apply(Math.sign(_))
  inline def floor: V = apply(Math.floor(_))
  inline def ceil: V = apply(Math.ceil(_))
  inline def mod(v: V): V = apply(v, Math.mod(_, _))
  inline def min(v: V): V = apply(v, Math.min(_, _))
  inline def max(v: V): V = apply(v, Math.max(_, _))
  inline def clamp(minVal: V, maxVal: V): V = apply(minVal, maxVal, Math.clamp(_, _, _))
  inline def mix(v: V, a: V): V = apply(v, a, Math.mix(_, _, _))
  inline def step(edge: V): V = apply(edge, Math.step(_, _))
  inline def smoothstep(edge0: V, edge1: V): V = apply(edge0, edge1, Math.smoothstep(_, _, _))
  inline def mod(s: T): V = apply(Math.mod(_, s))
  inline def min(s: T): V = apply(Math.min(_, s))
  inline def max(s: T): V = apply(Math.max(_, s))
  inline def clamp(minVal: T, maxVal: T): V = apply(Math.clamp(_, minVal, maxVal))
  inline def mix(v: V, a: T): V = apply(v, Math.mix(_, _, a))
  inline def step(edge: T): V = apply(Math.step(_, edge))
  inline def smoothstep(edge0: T, edge1: T): V = apply(Math.smoothstep(_, edge0, edge1))

  // -- Geometric Functions
  inline def normSq: T = dot(this)
  inline def norm: T = Math.sqrt(normSq)
  inline def normalize: V = if (isZero) this else this / norm
  inline def distanceSq(to: V): T = (to - this).normSq
  inline def distance(to: V): T = Math.sqrt(distanceSq(to))
}
