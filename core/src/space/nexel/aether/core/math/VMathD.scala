package space.nexel.aether.core.math
import space.nexel.aether.core.math.VMath

object VMathD extends VMath[Double] {

  val Zero = 0
  val One = 1

  val Log2 = Math.log(2)
  val Pi = Math.PI

  // ---- Common

  inline def abs(x: Double): Double = if (x > 0) x else -x
  inline def sign(x: Double): Double = if (x > 0) 1 else if (x < 0) -1 else 0
  inline def floor(x: Double): Double = Math.floor(x)
  inline def ceil(x: Double): Double = Math.ceil(x)
  inline def fract(x: Double): Double = x - Math.floor(x)

  inline def round(x: Double): Double = Math.round(x)

  inline def div(a: Double, b: Double): Double = a / b

  inline def mod(value: Double, modulo: Double): Double = {
    val result = value % modulo
    if (result < 0) result + modulo else result
  }

  inline def min(x: Double, y: Double): Double = if (x < y) x else y

  inline def min(a: Double, b: Double, c: Double): Double =
    if (a < b) (if (a < c) a else c) else (if (b < c) b else c)

  inline def max(x: Double, y: Double): Double = if (x > y) x else y

  inline def max(a: Double, b: Double, c: Double): Double =
    if (a > b) (if (a > c) a else c) else (if (b > c) b else c)

  /** clamp to [min, max] */
  inline def clamp(v: Double, min: Double, max: Double): Double =
    if (v > max) max else if (v < min) min else v

  /** clamp to [0, max] */
  inline def clamp(v: Double, max: Double): Double = if (v > max) max else if (v < 0) 0 else v

  /** clamp to [0, 1] */
  inline def clamp(v: Double): Double = if (v > 1) 1 else if (v < 0) 0 else v

  inline def mix(x: Double, y: Double, f: Double): Double = if (f <= 0) x else if (f >= 1) y else (1 - f) * x + f * y

  inline def step(x: Double, edge: Double = 0): Double = if (x < edge) 0 else 1

  inline def smoothstep(x: Double, edge0: Double, edge1: Double): Double = {
    val t = clamp((x - edge0) / (edge1 - edge0), 0.0, 1.0)
    t * t * (3.0 - 2.0 * t)
  }

  // ---- Exponential

  inline def pow(x: Double, y: Double): Double = Math.pow(x, y)

  inline def exp(x: Double): Double = Math.exp(x)

  inline def log(x: Double): Double = Math.log(x)

  inline def exp2(x: Double): Double = Math.exp(x * Log2)

  inline def log2(x: Double): Double = (Math.log(x) / Log2)

  inline def sqrt(x: Double): Double = Math.sqrt(x)

  inline def isqrt(x: Double): Double = (1 / Math.sqrt(x))

  // ---- Trigonometry

  inline def radians(degrees: Double): Double = Math.toRadians(degrees)

  inline def degrees(radians: Double): Double = Math.toDegrees(radians)

  inline def sin(a: Double): Double = Math.sin(a)

  inline def cos(a: Double): Double = Math.cos(a)

  inline def tan(a: Double): Double = Math.tan(a)

  inline def asin(x: Double): Double = Math.asin(x)

  inline def acos(v: Double): Double = Math.acos(v)

  inline def atan(yOverX: Double): Double = Math.atan(yOverX)

  inline def atan(y: Double, x: Double) = Math.atan2(y, x)


}
