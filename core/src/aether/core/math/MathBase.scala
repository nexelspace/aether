package aether.core.math

object MathBase {
  given mathI: MathBase[Int] = MathI
  given mathF: MathBase[Float] = MathF
  given mathD: MathBase[Double] = MathD
}

trait MathBase[T] {

  val Zero: T
  val One: T
  def Log2: T
  def Pi: T

  // ---- Common

  /** absolute value */
  def abs(x: T): T

  /** returns -1.0, 0.0, or 1.0 */
  def sign(x: T): T

  /** nearest integer <= x */
  def floor(x: T): T

  /** nearest integer >= x */
  def ceil(x: T): T

  /** x - floor(x */
  def fract(x: T): T

  def round(x: T): T

  def div(a: T, b: T): T

  /** modulus */
  def mod(value: T, modulo: T): T

  /** minimum value * */
  def min(x: T, y: T): T

  /** minimum value * */
  def min(a: T, b: T, c: T): T

  /** maximum value */
  def max(x: T, y: T): T

  /** maximum value */
  def max(a: T, b: T, c: T): T

  /** clamp to [min, max]
    * min(max(x, minVal), maxVal)
    */
  def clamp(v: T, min: T, max: T): T

  /** clamp to [0, max]
    * min(max(x, 0), maxVal)
    */
  def clamp(v: T, max: T): T

  /** clamp to [0, 1]
    * min(max(x, 0), 1)
    */
  def clamp(v: T): T

  /** linear blend of x and y */
  def mix(x: T, y: T, f: T): T

  /** 0.0 if x < edge, else 1.0 */
  def step(x: T, edge: T): T

  /** clip and smooth */
  def smoothstep(x: T, edge0: T, edge1: T): T

  // ---- Exponential

  def pow(x: T, y: T): T
  def exp(x: T): T
  def log(x: T): T
  def exp2(x: T): T
  def log2(x: T): T
  /** square root */
  def sqrt(x: T): T
  /** inverse square root */
  def isqrt(x: T): T

  // ---- Trigonometry

  /** degrees to radians */
  def radians(degrees: T): T
  /** radians to degrees */
  def degrees(radians: T): T
  /** sine */
  def sin(a: T): T
  /** cosine */
  def cos(a: T): T
  /** tangent */
  def tan(a: T): T
  /** arc sine */
  def asin(x: T): T
  /** arc cosine */
  def acos(v: T): T
  /** arc tangent */
  def atan(yOverX: T): T
  /** arc tangent */
  def atan(y: T, x: T): T

}
