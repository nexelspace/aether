package aether.core.math

import aether.core.types.Vec3F

object MathF extends MathBase[Float] {

  val Zero = 0
  val One = 1
  val Log2 = Math.log(2).toFloat
  val Pi = Math.PI.toFloat

  // ---- Common

  inline def abs(x: Float): Float = if (x > 0) x else -x
  inline def sign(x: Float): Float = if (x > 0) 1 else if (x < 0) -1 else 0
  inline def floor(x: Float): Float = Math.floor(x).toFloat
  inline def ceil(x: Float): Float = Math.ceil(x).toFloat
  inline def fract(x: Float): Float = x - Math.floor(x).toFloat

  inline def round(x: Float): Float = Math.round(x).toFloat

  inline def div(a: Float, b: Float): Float = a / b

  inline def mod(value: Float, modulo: Float): Float = {
    val result = value % modulo
    if (result < 0) result + modulo else result
  }

  inline def min(x: Float, y: Float): Float = if (x < y) x else y

  inline def min(a: Float, b: Float, c: Float): Float =
    if (a < b) (if (a < c) a else c) else (if (b < c) b else c)

  inline def max(x: Float, y: Float): Float = if (x > y) x else y

  inline def max(a: Float, b: Float, c: Float): Float =
    if (a > b) (if (a > c) a else c) else (if (b > c) b else c)

  /** clamp to [min, max] */
  inline def clamp(v: Float, min: Float, max: Float): Float =
    if (v > max) max else if (v < min) min else v

  /** clamp to [0, max] */
  inline def clamp(v: Float, max: Float): Float = if (v > max) max else if (v < 0) 0 else v

  /** clamp to [0, 1] */
  inline def clamp(v: Float): Float = if (v > 1) 1 else if (v < 0) 0 else v

  inline def mix(x: Float, y: Float, f: Float): Float = if (f <= 0) x else if (f >= 1) y else (1 - f) * x + f * y

  inline def step(x: Float, edge: Float = 0): Float = if (x < edge) 0 else 1

  inline def smoothstep(x: Float, edge0: Float, edge1: Float): Float = {
    val t = clamp((x - edge0) / (edge1 - edge0), 0.0f, 1.0f)
    t * t * (3.0f - 2.0f * t)
  }

  // ---- Exponential

  inline def pow(x: Float, y: Float): Float = Math.pow(x, y).toFloat

  inline def exp(x: Float): Float = Math.exp(x).toFloat

  inline def log(x: Float): Float = Math.log(x).toFloat

  inline def exp2(x: Float): Float = Math.exp(x * Log2).toFloat

  inline def log2(x: Float): Float = (Math.log(x) / Log2).toFloat

  inline def sqrt(x: Float): Float = Math.sqrt(x).toFloat

  inline def isqrt(x: Float): Float = (1 / Math.sqrt(x)).toFloat

  // ---- Trigonometry

  inline def radians(degrees: Float): Float = Math.toRadians(degrees).toFloat

  inline def degrees(radians: Float): Float = Math.toDegrees(radians).toFloat

  inline def sin(a: Float): Float = Math.sin(a).toFloat

  inline def cos(a: Float): Float = Math.cos(a).toFloat

  inline def tan(a: Float): Float = Math.tan(a).toFloat

  inline def asin(x: Float): Float = Math.asin(x).toFloat

  inline def acos(v: Float): Float = Math.acos(v).toFloat

  inline def atan(yOverX: Float): Float = Math.atan(yOverX).toFloat

  inline def atan(y: Float, x: Float) = Math.atan2(y, x).toFloat


}
