package aether.core.util

import aether.core.types.Vec3D
import aether.core.math.MathF
import aether.core.math.MathD

object Colors {

  def rgb(r: Int, g: Int, b: Int): Int = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff)

  def argb(a: Float, r: Float, g: Float, b: Float): Int = {
    (MathF.clamp(a * 255, 255).toInt << 24) |
      (MathF.clamp(r * 255, 255).toInt << 16) |
      (MathF.clamp(g * 255, 255).toInt << 8) |
      MathF.clamp(b * 255, 255).toInt
  }

  def argb(a: Double, r: Double, g: Double, b: Double): Int =
    (MathD.clamp(a * 255, 255).toInt << 24) |
      (MathD.clamp(r * 255, 255).toInt << 16) |
      (MathD.clamp(g * 255, 255).toInt << 8) |
      MathD.clamp(b * 255, 255).toInt

  def rgb(r: Float, g: Float, b: Float): Int =
    0xff000000 |
      (MathF.clamp(r * 255, 255).toInt << 16) |
      (MathF.clamp(g * 255, 255).toInt << 8) |
      MathF.clamp(b * 255, 255).toInt

  def rgb(v: Vec3D): Int = rgb(v.x, v.y, v.z)

  def rgb(r: Double, g: Double, b: Double): Int = {
    0xff000000 |
      (MathD.clamp(r * 255, 255).toInt << 16) |
      (MathD.clamp(g * 255, 255).toInt << 8) |
      MathD.clamp(b * 255, 255).toInt
  }

  def argb(a: Int, r: Int, g: Int, b: Int): Int =
    ((a & 0xff) << 24) | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff)

  def argb(argb: Int): (Int, Int, Int, Int) =
    ((argb >>> 24) & 0xff, (argb >> 16) & 0xff, (argb >> 8) & 0xff, argb & 0xff)

  def mix(color1: Int, color2: Int, fraction: Float) = {
    if (fraction <= 0) color1
    else if (fraction >= 1) color2
    else {
      val f     = (fraction * 0x100).toInt
      val ifrac = 0x100 - f
      ((((color1 & 0x00ff00ff) * ifrac + (color2 & 0x00ff00ff) * f) & 0xff00ff00) >> 8) |
        ((((color1 >> 8) & 0x00ff00ff) * ifrac + ((color2 >> 8) & 0x00ff00ff) * f) & 0xff00ff00)
    }
  }

  def mix(color1: Int, color2: Int) = {
    (((((color1 & 0x00ff00ff) << 7) + ((color2 & 0x00ff00ff) << 7)) & 0xff00ff00) >> 8) |
      (((((color1 >> 8) & 0x00ff00ff) << 7) + ((color2 >> 8) & 0x00ff00ff) << 7) & 0xff00ff00)
  }

  /** Convert HSL to RGB.
    * From http://axonflux.com/handy-rgb-to-hsl-and-rgb-to-hsv-color-model-c
    * @param h Hue [0,1], wrapped
    * @param s Saturation [0,1], clamped
    * @param l Lightness [0,1], clamped
    * @return RGB color as 0x00rrggbb
    */
  def hsl(h: Float, s: Float, l: Float, alpha: Float = 1): Int = {
    val sc = MathF.clamp(s, 1)
    val lc = MathF.clamp(l, 1)
    val q  = if (lc < .5f) lc * (1f + sc) else lc + sc - lc * sc
    val p  = 2f * lc - q
    def hue2rgb(t: Float) = MathF.mod(t, 1) match {
      case tm if (tm < 1f / 6f) => p + (q - p) * 6 * tm
      case tm if (tm < 1f / 2f) => q
      case tm if (tm < 2f / 3f) => p + (q - p) * (2f / 3f - tm) * 6
      case _                    => p
    }
    val r = hue2rgb(h + 1f / 3f)
    val g = hue2rgb(h)
    val b = hue2rgb(h - 1f / 3f)
    argb(alpha, r, g, b)
  }

  /** Get color by index in a way that RGB space is utilised effectively.
    */
  def indexed(aIndex: Int): Int = {
    var index   = aIndex
    var r, g, b = 0
    var mask    = 1
    var i       = 8
    while (index > 0 && i > 0) {
      r |= index & mask
      g |= (index >> 1) & mask
      b |= (index >> 2) & mask
      index >>= 2
      mask <<= 1
      index &= ~(mask - 1) // for optimising loop termination
      i -= 1
    }
    r = 0xff - ((r - 1) & 0xff)
    g = 0xff - ((g - 1) & 0xff)
    b = 0xff - ((b - 1) & 0xff)
    Bits.reverseBytes((r << 16) | (g << 8) | b)
  }

  /** Expand 16 bit color to 32 bit. */
  private def expandColor(color: Int): Int = {
    val c0 = color & 0xf
    val c1 = color & 0xf0
    val c2 = color & 0xf00
    val c3 = color & 0xf000
    c0 | (c0 << 4) | (c1 << 4) | (c1 << 8) | (c2 << 8) | (c2 << 12) | (c3 << 12) | (c3 << 16)
  }

  /** Parse hex color string to integer. */
  def parse(color: String): Int = {
    val Data = """(|#|0x)([0-9a-fA-F]+)""".r
    color match {
      case Data(prefix, c) =>
        c.length match {
          case 3 => expandColor(Integer.parseInt(c, 16)) | 0xff000000
          case 4 => expandColor(Integer.parseInt(c, 16))
          case 6 => Integer.parseInt(c, 16) | 0xff000000
          case 8 => java.lang.Long.parseLong(c, 16).toInt
          case _ => throw new IllegalArgumentException(s"Invalid color: $c")
        }
      case _ => sys.error(s"Invalid color: $color")
    }
  }
}
