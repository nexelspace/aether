package space.nexel.aether.core.types

import math.Numeric.Implicits.infixNumericOps
import math.Fractional.Implicits.infixFractionalOps
import math.Integral.Implicits.infixIntegralOps
import math.Ordering.Implicits.infixOrderingOps
import space.nexel.aether.core.math.VMathF
import space.nexel.aether.core.math.VMathD
import space.nexel.aether.core.math.VMath
import space.nexel.aether.core.math.VMathI
import scala.annotation.targetName

trait Vec2[T, VV <: Vec2[T, _]](using math: VMath[T], num: Numeric[T]) extends IndexedSeq[T] {
  val x: T
  val y: T

  // type V = Vec2[T]
  type V <: Vec2[T, _] //= this.type

  override val length = 2

  /** Create new instance of same type */
  protected def create(x: T, y: T): V
  protected def instance: V

  def apply(index: Int): T = index match {
    case 0 => x
    case 1 => y
    case i => throw IndexOutOfBoundsException(s"Index out of range: $i")
  }

  inline def isZero: Boolean = x == num.zero && y == num.zero

  // def ++ : V
  // def -- : V
  inline def unary_- : V = create(-x, -y)
  // def ! : Vec2[Boolean]

  inline def *(s: T): V = create(x * s, y * s)
  inline def /(s: T): V = create(math.div(x, s), math.div(y, s))
  inline def +(s: T): V = create(x + s, y + s)
  inline def -(s: T): V = create(x - s, y - s)

  // @targetName("mulVec2")
  inline def *(v: V): V = create(x * v.x, y * v.y)
  inline def /(v: V): V = create(math.div(x, v.x), math.div(y, v.y))
  inline def +(v: V): V = create(x + v.x, y + v.y)
  inline def -(v: V): V = create(x - v.x, y - v.y)

  inline def ==(v: V): Boolean = x == v.x && y == v.y
  inline def !=(v: V): Boolean = x != v.x || y != v.y
  inline def <(v: V) = x < v.x && y < v.y
  inline def >(v: V) = x > v.x && y > v.y
  inline def <=(v: V) = x <= v.x && y <= v.y
  inline def >=(v: V) = x >= v.x && y >= v.y

  // Common Functions

  inline def abs: V = create(x.abs, y.abs)
  inline def sign: V = create(x.sign, y.sign)
  inline def floor: V = create(math.floor(x), math.floor(y))
  inline def ceil: V = create(math.ceil(x), math.ceil(y))

  inline def mod(v: V): V = create(math.mod(x, v.x), math.mod(y, v.y))
  inline def min(v: V): V = create(math.min(x, v.x), math.min(y, v.y))
  inline def max(v: V): V = create(math.max(x, v.x), math.max(y, v.y))
  inline def clamp(minVal: V, maxVal: V): V = create(math.clamp(x, minVal.x, maxVal.x), math.min(math.max(y, minVal.y), maxVal.y))
  inline def mix(v: V, a: V): V = create(math.mix(x, v.x, a.x), math.mix(y, v.y, a.y))
  inline def step(edge: V): V = create(math.step(x, edge.x), math.step(y, edge.y))
  inline def smoothstep(edge0: V, edge1: V): V = create(math.smoothstep(edge0.x, edge1.x, x), math.smoothstep(edge0.x, edge1.y, y))

  inline def mod(s: T): V = mod(create(s, s))
  inline def min(s: T): V = min(create(s, s))
  inline def max(s: T): V = max(create(s, s))
  inline def clamp(minVal: T, maxVal: T): V = create(math.clamp(x, minVal, maxVal), math.min(math.max(y, minVal), maxVal))
  inline def mix(v: V, a: T): V = create(math.mix(x, v.x, a), math.mix(y, v.y, a))
  inline def step(edge: T): V = create(math.step(x, edge), math.step(y, edge))
  inline def smoothstep(edge0: T, edge1: T): V = create(math.smoothstep(edge0, edge1, x), math.smoothstep(edge0, edge1, y))

  // Geometric Functions

  inline def normSq: T = x * x + y * y
  inline def norm: T = math.sqrt(normSq)
  inline def distanceSq(to: V): T = {
    val r = to - this
    r.normSq
  }
  inline def distance(to: V): T = math.sqrt(distanceSq(to))
  inline def dot(v: V): T = x * v.x + y * v.y
  inline def cross(v: V): T = (x * v.y) - (y * v.x)
  inline def normalize: V = if (isZero) instance else this / norm

  // Vector Relational Functions
  //TODO

  // --

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
  // given Conversion[Vec2[Int], Vec2I] = (v: Vec2[Int]) => v

  val Zero = Vec2I(0)
  val One = Vec2I(1)
  def apply(v: (Int, Int)) = new Vec2I(v._1, v._2)
  def apply(v: Int): Vec2I = new Vec2I(v, v)
  def apply(a: Array[Int], index: Int = 0) =
    new Vec2I(a(index + 0), a(index + 1))
}

case class Vec2I(x: Int, y: Int) extends Vec2[Int, Vec2I] {
  inline def create(x: Int, y: Int): Vec2I = Vec2I(x, y)

  def &(c: Int): Vec2I = new Vec2I(x & c, y & c)
  def |(c: Int): Vec2I = new Vec2I(x | c, y | c)
  def >>(c: Int): Vec2I = new Vec2I(x >> c, y >> c)
  def <<(c: Int): Vec2I = new Vec2I(x << c, y << c)
}

object Vec2F {  
  // given Conversion[Vec2[Float], Vec2F] = (v: Vec2[Float]) => v

  val Zero = Vec2F(0)
  val One = Vec2F(1)
  def apply(v: (Float, Float)) = new Vec2F(v._1, v._2)
  def apply(v: Float) = new Vec2F(v, v)
  def apply(a: Array[Float], index: Int = 0) =
    new Vec2F(a(index + 0), a(index + 1))
}

case class Vec2F(x: Float, y: Float) extends Vec2[Float, Vec2F] {
  inline def create(x: Float, y: Float): Vec2F = Vec2F(x, y)
}

object Vec2D {  
  // given Conversion[Vec2[Double], Vec2D] = (v: Vec2[Double]) => v

  val Zero = Vec2D(0)
  val One = Vec2D(1)
  def apply(v: (Double, Double)) = new Vec2D(v._1, v._2)
  def apply(v: Double) = new Vec2D(v, v)
  def apply(a: Array[Double], index: Int = 0) =
    new Vec2D(a(index + 0), a(index + 1))
}

case class Vec2D(x: Double, y: Double) extends Vec2[Double, Vec2D] {
  inline def create(x: Double, y: Double) = Vec2D(x, y)
}
