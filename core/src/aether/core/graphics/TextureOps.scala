package aether.core.graphics

import Texture.Format
import aether.core.buffers.ElementBuffer
import aether.core.types.Color

object TextureOps {
  
//  def getBitsPerPixel(pixelFormat: Format): Int = pixelFormat match {
//    case UNDEFINED      => 0
//    case PVRTC_2BIT     => 2
//    case PVRTC_2BIT_RGB => 2
//    case DXT1           => 4
//    case ETC1           => 4
//    case PVRTC_4BIT     => 4
//    case PVRTC_4BIT_RGB => 4
//    case LUMINANCE_8    => 8
//    case DXT3           => 8
//    case DXT5           => 8
//    case RGB_565        => 16
//    case RGB_1555       => 16
//    case RGBA_4444      => 16
//    case RGBA_5551      => 16
//    case DEPTH_16       => 16
//    case LUMINANCE_16   => 16
//    case R_16           => 16
//    case RGB_888        => 24
//    case BGR_888        => 24
//    case SRGB_888       => 24
//    case DEPTH_24       => 24
//    case ARGB_8888      => 32
//    case RGBA_8888      => 32
//    case RGB_8888       => 32
//    case BGRA_8888      => 32
//    case ABGR_8888      => 32
//    case DEPTH_32       => 32
//    case DEPTH_32F      => 32
//    case RG_16          => 32
//    case _              => sys.error(s"Invalid pixel format: $pixelFormat")
//  }

//  def hasAlpha(pixelFormat: Format): Boolean = pixelFormat match {
//    case RGBA_4444 | RGBA_5551 | RGBA_8888 => true
//    case ABGR_8888                         => true
//    case _                                 => false
//  }

//  def bytesPerPixel(pixelFormat: Format): Int = getBitsPerPixel(pixelFormat) >> 3

  def getARGB(buffer: ElementBuffer, format: Format): Color = argbReader(buffer, format)()

  def putARGB(buffer: ElementBuffer, format: Format, color: Color, premultiply: Boolean = false) = argbWriter(buffer, format, premultiply)(color)

  def putARGB(argb: Array[Int], buffer: ElementBuffer, format: Format, premultiply: Boolean) = {
    val writer = argbWriter(buffer, format, premultiply)
    argb.foreach { c => writer(Color(c)) }
  }

  def argbReader(buffer: ElementBuffer, format: Format): () => Color = {
    format match {
      case Format.BGRA_8888 => { () =>
        val b = buffer.getI()
        val g = buffer.getI()
        val r = buffer.getI()
        val a = buffer.getI()
        Color(a, r, g, b)
      }
      case Format.BGR_888 => { () =>
        val b = buffer.getI()
        val g = buffer.getI()
        val r = buffer.getI()
        Color(0xff, r, g, b)
      }
      case Format.RGBA_8888 => { () =>
        val r = buffer.getI()
        val g = buffer.getI()
        val b = buffer.getI()
        val a = buffer.getI()
        Color(a, r, g, b)
      }
      case Format.RGB_888 => { () =>
        val r = buffer.getI()
        val g = buffer.getI()
        val b = buffer.getI()
        Color(0xff, r, g, b)
      }
      case _ =>
        sys.error(s"Unsupported pixel format: format")
    }
  }

  def argbWriter(buffer: ElementBuffer, format: Format, premultiply: Boolean = false): (Color) => Unit = {
    format match {
      case Format.ARGB_8888 if (premultiply) => {
        (color: Color) =>
          buffer.putI(color.a)
          buffer.putI(color.RxA)
          buffer.putI(color.GxA)
          buffer.putI(color.BxA)
      }
      case Format.ARGB_8888 if (!premultiply) => {
        (color: Color) =>
          buffer.putI(color.a)
          buffer.putI(color.r)
          buffer.putI(color.g)
          buffer.putI(color.b)
      }
      case Format.RGB_888 => {
        (color: Color) =>
          buffer.putI(color.r)
          buffer.putI(color.g)
          buffer.putI(color.b)
      }
      case Format.RGBA_8888 if (premultiply) => {
        (color: Color) =>
          buffer.putI(color.RxA)
          buffer.putI(color.GxA)
          buffer.putI(color.BxA)
          buffer.putI(color.a)
      }
      case Format.RGBA_8888 if (!premultiply) => {
        (color: Color) =>
          buffer.putI(color.r)
          buffer.putI(color.g)
          buffer.putI(color.b)
          buffer.putI(color.a)
      }
      case Format.BGRA_8888 if (!premultiply) => {
        (color: Color) =>
          buffer.putI(color.b)
          buffer.putI(color.g)
          buffer.putI(color.r)
          buffer.putI(color.a)
      }
      case Format.BGR_888 => {
        (color: Color) =>
          buffer.putI(color.b)
          buffer.putI(color.g)
          buffer.putI(color.r)
      }
      case _ =>
        sys.error(s"Unsupported pixel format: $format, $premultiply")
    }
  }
}