package aether.jvm.texture

import aether.core.graphics.Texture.FileFormat

import java.io.BufferedInputStream
import java.io.IOException
import javax.imageio.ImageIO
import aether.core.graphics.Texture.Format

object TextureDecoderJ2SE {

  def decodePNG(stream: BufferedInputStream, format: Format): TextureData = {
    decodeImage(stream, format)
  }
//  def decodePNG(stream: BufferedInputStream, format: Format): TextureData = {
//    val decoder = new PNGDecoder(stream)
//    val data = new TextureData(decoder.getWidth, decoder.getHeight, format)
//    val decFormat = format match {
//      case Format.ABGR_8888 => PNGDecoder.Format.ABGR
//      //      case Format. => PNGDecoder.Format.ALPHA
//      case Format.BGRA_8888 => PNGDecoder.Format.BGRA
//      case Format.LUMINANCE_8 => PNGDecoder.Format.LUMINANCE
//      //      case Format. => PNGDecoder.Format.LUMINANCE_ALPHA
//      case Format.RGB_888 => PNGDecoder.Format.RGB
//      case Format.RGBA_8888 => PNGDecoder.Format.RGBA
//      case _ => throw new IOException("Unsupported format: " + format.toInt.toHexString)
//    }
//    decoder.decode(data.buffer, data.sizeX * 4, decFormat)
//    data.buffer.flip()
//    data
//  }

  def decodeImage(stream: BufferedInputStream, format: Format): TextureData = {
    val image = ImageIO.read(stream)
    if (image == null) {
      throw new IOException("No ImageReader for image")
    }
    assert(format == Format.RGBA_8888, "Unsupported format: " + format)
    val data = new TextureData(image.getWidth, image.getHeight, Format.RGBA_8888)
    val array = Array.ofDim[Int](data.sizeX * data.sizeY)
    image.getRGB(0, 0, data.sizeX, data.sizeY, array, 0, data.sizeX)
    TextureDecoder.ARGBtoRGBA(data.buffer, array)
    data.buffer.flip()
    data
  }
}