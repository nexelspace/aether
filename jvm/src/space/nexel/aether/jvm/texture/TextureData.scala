package space.nexel.aether.jvm.texture

import java.nio.ByteBuffer

import TextureData._
import space.nexel.aether.core.graphics.Texture
import space.nexel.aether.jvm.buffers.JvmBuffer

object TextureData {

  def createDummy(size: Int): TextureData = {
    val data = new TextureData(size, size, Texture.Format.RGBA_8888)
    for (i <- 0 until size * size) {
      Seq(0xff, 0x00, 0xfff ,0x88).foreach {
        case b => data.buffer.putI(b.toByte)
      }
    }
    data.buffer.flip()
    data
  }
}

class TextureData(val sizeX: Int, val sizeY: Int, val format: Texture.Format) {
  val typ = format.componentType
  val buffer = JvmBuffer.create(typ, format.bytesPerPixel / typ.bytes * sizeX * sizeY)

  var fileFormat = Texture.Format.UNDEFINED
  var isPremultiplied = false

  override def toString = s"[$sizeX, $sizeY, $format]"

}
