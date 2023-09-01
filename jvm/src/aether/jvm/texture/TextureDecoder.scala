package aether.jvm.texture

import aether.core.buffers.ElementBuffer
import aether.core.graphics.Texture.FileFormat

import java.io.BufferedInputStream
import java.nio.ByteBuffer

object TextureDecoder {
  def decode(stream: BufferedInputStream, fileFormat: FileFormat) = {
    ???
  }

  def ARGBtoRGBA(target: ElementBuffer, source: Array[Int]) = {
    source foreach { argb =>
      target.putB((argb >> 16).toByte)
      target.putB((argb >> 8).toByte)
      target.putB((argb >> 0).toByte)
      target.putB((argb >> 24).toByte)
    }
  }
}