package space.nexel.aether.core.types

import scala.language.implicitConversions
import space.nexel.aether.core.types.Vec4F
import space.nexel.aether.core.math.VMathI
import space.nexel.aether.core.util.Strings

object Color {
  val Transparent = Color(0)
  val Black = Color(0xff000000)
  val White = Color(0xffffffff)
  val Red = Color(0xffff0000)
  val Green = Color(0xff00ff00)
  val Blue = Color(0xff0000ff)
  val Yellow = Color(0xffffff00)
  val Purple = Color(0xffff00ff)
  val Cyan = Color(0xff00ffff)

  /** Create color from ARGB int. */
  def apply(argb: Int): Color = new Color(argb)
  /** Create color from ARGB bytes. */
  def apply(a: Int, r: Int, g: Int, b: Int): Color = new Color(((a & 0xff) << 24) | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff))
  /** Create color from RGB bytes. */
  def apply(r: Int, g: Int, b: Int): Color = new Color(0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff))

  /** Create color from ARGB floats. */
  def ARGB(a: Float, r: Float, g: Float, b: Float): Color = Color((a * 255).toInt, (r * 255).toInt, (g * 255).toInt, (b * 255).toInt)
  /** Create color from RGB floats. */
  def RGB(r: Float, g: Float, b: Float): Color = Color(255, (r * 255).toInt, (g * 255).toInt, (b * 255).toInt)
  def RGB(lum: Float): Color = {
    val c = (lum * 255).toInt
    Color(255, c, c, c)
  }

  def abgr(abgr: Int): Color = new Color((abgr & 0xff00ff00) | ((abgr >> 16) & 0xff) | ((abgr << 16) & 0xff0000))
  def argb(argb: Int): Color = new Color(argb)
  def rgba(rgba: Int): Color = new Color((rgba>>>8) | (rgba<<24))

  implicit def toColor(argb: Int): Color = new Color(argb)
  implicit def fromColor(color: Color): Int = color.argb

}

class Color(val argb: Int) extends AnyVal {
  def a: Int = argb >>> 24
  def r: Int = (argb >>> 16) & 0xff
  def g: Int = (argb >>> 8) & 0xff
  def b: Int = argb & 0xff

  def RxA: Int = r * a / 255
  def GxA: Int = g * a / 255
  def BxA: Int = b * a / 255

  def components: (Int, Int, Int, Int) = (a, r, g, b)

  /** Make new color by multiplying RGB components with alpha. */
  def prempultiply: Color = Color(a, RxA, GxA, BxA)

  def alpha(scale: Float) = Color(((a & 0xff) * scale).toInt, r, g, b)

  def *(c: Color): Color = {
    def mul(a: Int, b: Int): Int = {
      (((a << 1) | ((a >> 7) & 1)) * ((b << 1) | ((b >> 7) & 1))) >> 10
    }
    Color(mul(a, c.a), mul(r, c.r), mul(g, c.g), mul(b, c.b))
  }

  def rgbaF = Vec4F(r / 255f, g / 255f, b / 255f, a / 255f)
  def rgba255 = Vec4F(r.toFloat, g.toFloat, b.toFloat, a.toFloat)
  def rgba = (argb << 8) | (argb >>>24)

  /** HSL lightness component. */
  def lightness: Float = (VMathI.max(r, g, b) + VMathI.min(r, g, b)) / 510f

  override def toString: String = "#" + Strings.toHex(argb, 8)

}
