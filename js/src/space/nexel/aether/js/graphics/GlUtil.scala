package space.nexel.aether.js.graphics

import org.scalajs.dom.raw.{WebGLRenderingContext => GL}
import space.nexel.aether.core.graphics.Texture.Format

object GlUtil {
  // GL constants
  val R32UI = 0x8236
  val RED_INTEGER = 0x8D94
  val RGBA8UI = 0x8D7C
  val RGBA_INTEGER = 0x8D99

  def toGlInternalFormat(format: Format): Int = format match {
    case Format.R32UI        => R32UI
    case Format.RGBA_8888    => GL.RGBA
    case Format.RGBA_8888_UI => RGBA8UI
    case _                   => sys.error(s"Unsupported pixel format: $format")
  }

  def toGlFormat(format: Format): Int = format match {
    case Format.R32UI        => RED_INTEGER
    case Format.RGBA_8888    => GL.RGBA
    case Format.RGBA_8888_UI => RGBA_INTEGER
    case _                   => sys.error(s"Unimplemented for $format")
  }
}
