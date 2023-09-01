package aether.core.math
import aether.core.math.VMath
import aether.core.util.Bits

object VMathI extends VMath[Int] {

  val Zero = 0
  val One = 1
  def Log2 = ???
  def Pi = ???

  // ---- Common

  inline def abs(x: Int): Int = if (x > 0) x else -x
  inline def sign(x: Int): Int = if (x > 0) 1 else if (x < 0) -1 else 0
  inline def floor(x: Int): Int = ??? // Math.floor(x)
  inline def ceil(x: Int): Int = ??? // Math.ceil(x)
  inline def fract(x: Int): Int = ??? // x - Math.floor(x)

  inline def round(x: Int): Int = Math.round(x)

  inline def div(a: Int, b: Int): Int = a / b

  inline def mod(value: Int, modulo: Int): Int = {
    val result = value % modulo
    if (result < 0) result + modulo else result
  }

  inline def min(x: Int, y: Int): Int = if (x < y) x else y

  inline def min(a: Int, b: Int, c: Int): Int =
    if (a < b) (if (a < c) a else c) else (if (b < c) b else c)

  inline def max(x: Int, y: Int): Int = if (x > y) x else y

  inline def max(a: Int, b: Int, c: Int): Int =
    if (a > b) (if (a > c) a else c) else (if (b > c) b else c)

  /** clamp to [min, max] */
  inline def clamp(v: Int, min: Int, max: Int): Int =
    if (v > max) max else if (v < min) min else v

  /** clamp to [0, max] */
  inline def clamp(v: Int, max: Int): Int = if (v > max) max else if (v < 0) 0 else v

  /** clamp to [0, 1] */
  inline def clamp(v: Int): Int = if (v > 1) 1 else if (v < 0) 0 else v

  inline def mix(x: Int, y: Int, f: Int): Int = if (f <= 0) x else if (f >= 1) y else (1 - f) * x + f * y

  inline def step(x: Int, edge: Int = 0): Int = if (x < edge) 0 else 1

  inline def smoothstep(x: Int, edge0: Int, edge1: Int): Int = ???

  // ---- Exponential

  inline def pow(x: Int, y: Int): Int = ??? // Math.pow(x, y)

  inline def exp(x: Int): Int = ??? // Math.exp(x)

  inline def log(x: Int): Int = ??? // Math.log(x)

  inline def exp2(x: Int): Int = ??? // Math.exp(x * Log2)

  inline def log2(x: Int): Int = Bits.indexOfMSB(x)

  inline def sqrt(x: Int): Int = ??? // Math.sqrt(x)

  inline def isqrt(x: Int): Int = ??? // (1 / Math.sqrt(x))

  // ---- Trigonometry

  inline def radians(degrees: Int): Int = ??? // Math.toRadians(degrees)

  inline def degrees(radians: Int): Int = ??? // Math.toDegrees(radians)

  inline def sin(a: Int): Int = ??? // Math.sin(a)

  inline def cos(a: Int): Int = ??? // Math.cos(a)

  inline def tan(a: Int): Int = ??? // Math.tan(a)

  inline def asin(x: Int): Int = ??? // Math.asin(x)

  inline def acos(v: Int): Int = ??? // Math.acos(v)

  inline def atan(yOverX: Int): Int = ??? // Math.atan(yOverX)

  inline def atan(y: Int, x: Int) = ??? // Math.atan2(y, x)

}
