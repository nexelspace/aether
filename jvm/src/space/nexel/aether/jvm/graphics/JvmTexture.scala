package space.nexel.aether.jvm.graphics

import space.nexel.aether.core.graphics.*
import space.nexel.aether.core.platform.*
import space.nexel.aether.core.graphics.Texture.Config
import space.nexel.aether.core.graphics.Texture.TextureFactory
import space.nexel.aether.core.types.Vec2I
import space.nexel.aether.core.types.Color
import space.nexel.aether.core.buffers.NativeBuffer
import space.nexel.aether.core.buffers.ByteBuffer

import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL13._
import org.lwjgl.opengl.GL30._

import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import space.nexel.aether.core.base.Ref
import space.nexel.aether.jvm.texture.*
import space.nexel.aether.core.graphics.TextureOps
import space.nexel.aether.core.graphics.Texture.FileFormat
import space.nexel.aether.core.graphics.Texture.Format
import space.nexel.aether.core.platform.Dispatcher
import  space.nexel.aether.jvm.buffers.*
import space.nexel.aether.core.types.RectI

object JvmTexture {
  def factory(using g: Graphics) = new Resource.Factory[Texture, Texture.Config] {
    given TextureFactory = this
    def createThis(config: Config) = {
      val data = if (config.data.isDefined) {
        fromStream(new BufferedInputStream(new ByteArrayInputStream(config.data.get)), config.fileFormat, config.format)
      } else {
        assert(config.size.isDefined)
        val size = config.size.get
        val data = new TextureData(size.x, size.y, config.format)
        if (config.argb.isDefined) {
          TextureOps.putARGB(config.argb.get, data.buffer, data.format, data.isPremultiplied)
          data.buffer.flip()
          // Log("Created texture "+config.argb+" -> "+data.buffer)
        }
        data
      }

      // Log("Creating texture "+config+" -> "+data)
      val r = new JvmTexture(config, data)
      r
    }

    override def loadThis(ref: Ref, config: Config)(using dispatcher: Dispatcher): Resource[Texture] = {
      ref.loadByteBuffer().map { buffer =>
        val stream = new BufferedInputStream(new ByteArrayInputStream(buffer.toByteArray))
        val data = fromStream(stream, config.fileFormat, config.format)
        assert(data != null, "Not found: " + ref)
        new JvmTexture(config, data)
      }

      // val stream = ref.base.asInstanceOf[JvmBase].getInputStream(ref.path)
      // val data = fromStream(stream, config.fileFormat, config.format)
      // assert(data != null, "Not found: " + ref)
      // Future(new NTexture(config, data))
    }

    def fromStream(stream: BufferedInputStream, fileFormat: FileFormat, format: Format): TextureData = {
      try {
        fileFormat match {
          case FileFormat.UNDEFINED | FileFormat.PNG ⇒
            try {
              stream.mark(256)
              TextureDecoderJ2SE.decodePNG(stream, format)
            } catch {
              case e: IOException ⇒
                Log(e.getMessage)
                stream.reset()
                TextureDecoderJ2SE.decodeImage(stream, format)
            }
          case FileFormat.JPEG ⇒
            TextureDecoderJ2SE.decodeImage(stream, format)
          case FileFormat.PVR | FileFormat.DDS | FileFormat.PKM ⇒
            TextureDecoder.decode(stream, fileFormat)
          case _ ⇒
            scala.sys.error(s"Invalid file format: $fileFormat")
        }
      } catch {
        case e: IOException ⇒
//        Log(e.getMessage)
          throw new RuntimeException("Failed to decode image: " + e.getMessage)
        // TextureData.createDummy(8)
//        null
      } finally {
        try {
          stream.close()
        } catch {
          case e: IOException ⇒
        }
      }
    }
  }
}

class JvmTexture(val config: Config, data: TextureData)(using g: Graphics, factory: TextureFactory) extends Texture {

  override val format = data.format

  //  lazy val area: RectI = ??? //TODO: use area instead of size (jovr, subtextures)

  override val isPremultiplied = data.isPremultiplied
//  var buf: Option[ByteBuffer] = Some(data.buffer)

//  val bufferType = Texture.componentType(config.format)

  // buffer is always defined on JVM
  val buffer: Option[NativeBuffer] = Some(data.buffer)

  // NativeBuffer.create(bufferType, config.format.bytesPerPixel * size.x * size.y)

  val glTextureId = glGenTextures()

  //  val glPixelFormat = GlUtil.toGlPixelFormat(format)
  val glInternalFormat = GlUtil.toGlInternalFormat(format)
  val glFormat = GlUtil.toGlFormat(format)

  upload()

  def upload() = {
    // assert(buf.isDefined)
    glBindTexture(GL_TEXTURE_2D, glTextureId)
    glPixelStorei(GL_UNPACK_ALIGNMENT, 4) // TODO
    val compType = GlUtil.getComponentType(format)
    // Log(s"Upload texture $glInternalFormat, $glFormat, $compType")
    def texImage(target: Int) = {
      nativeBuffer match {
        case b: BufferB => glTexImage2D(target, 0, glInternalFormat, size.x, size.y, 0, glFormat, compType, b.buffer)
        case b: BufferS => glTexImage2D(target, 0, glInternalFormat, size.x, size.y, 0, glFormat, compType, b.buffer)
        case b: BufferI => glTexImage2D(target, 0, glInternalFormat, size.x, size.y, 0, glFormat, compType, b.buffer)
        case b: BufferF => glTexImage2D(target, 0, glInternalFormat, size.x, size.y, 0, glFormat, compType, b.buffer)
      }
    }
    if (config.flags.has(Texture.Flag.Cubemap)) {
      // for (i<-0 until 6) texImage()
      ???
    } else {
      texImage(GL_TEXTURE_2D)
    }

    if (hasMipmaps) glGenerateMipmap(GL_TEXTURE_2D)
    if (!isReadable && !isWritable) {
      // release pixel data
//      buf = None
    }
  }

  def bind(textureIndex: Int) = {
    prepareRender()
    glActiveTexture(GL_TEXTURE0 + textureIndex)
    glBindTexture(GL_TEXTURE_2D, glTextureId)
    val filter = g.filter
    if (filter == Graphics.Filter.Nearest) {
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
    } else {
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, if (hasMipmaps) GL_LINEAR_MIPMAP_LINEAR else GL_LINEAR)
    }
  }

  private def preparePixelAccess() = {
    assert(isReadable || isWritable, "Texture has no pixel access")
    if (rendered) {
      rendered = false
      glBindFramebuffer(GL_FRAMEBUFFER, 0)
      glBindTexture(GL_TEXTURE_2D, glTextureId)
      glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, glTextureId, 0)
      val pFormat = GlUtil.toGlPixelFormat(format)

      nativeBuffer match {
        case b: BufferB => glReadPixels(0, 0, size.x, size.y, pFormat, GL_UNSIGNED_BYTE, b.buffer)
        case b: BufferS => glReadPixels(0, 0, size.x, size.y, pFormat, GL_UNSIGNED_SHORT, b.buffer)
        case b: BufferI => glReadPixels(0, 0, size.x, size.y, pFormat, GL_UNSIGNED_INT, b.buffer)
        case b: BufferF => glReadPixels(0, 0, size.x, size.y, pFormat, GL_FLOAT, b.buffer)
      }
    }
  }

  def release() = {
    factory.released(this)
    glDeleteTextures(new Array[Int](glTextureId))
  }

  // ---- public interface

  def getARGB(x: Int, y: Int): Int = {
    preparePixelAccess()
//    val buffer = this.buf.get
    val inside = x >= 0 && x < size.x && y >= 0 && y < size.y
    assert(inside, s"Invalid coords $x: $size.x, $y: $size.y")
    if (inside) {
      val bpp = format.bytesPerPixel
      val p = (y * size.x + x) * bpp
      assert(
        p < nativeBuffer.capacity,
        s"Invalid position $x, $y, $size.x, $size.y, $bpp: $p in ${nativeBuffer.capacity}"
      )
      nativeBuffer.position = p
      pixelReader().argb
    } else 0
  }

  def setARGB(argb: Int, x: Int, y: Int): Unit = {
    assert(x >= 0 && x < size.x && y >= 0 && y < size.y, s"Out of bounds $x, $y in $size.x, $size.y")
    preparePixelAccess()
//    val buffer = this.buf.get
    val index = (y * size.x + x) * format.bytesPerPixel
    assert(index >= 0 && index < nativeBuffer.limit, s"Invalid index $index, $buffer")
    nativeBuffer.position = index
    pixelWriter(Color(argb))
    modified = true
  }

  def setARGB(array: Array[Int], offset: Int, stride: Int, area: RectI): Unit = {
    preparePixelAccess()
    val realStride = if (stride == 0) size.x else stride
//    val buffer = this.buf.get
    for (y ← 0 until area.size.y) {
      nativeBuffer.position = ((y + area.y) * size.x + area.x) * format.bytesPerPixel
      var i = offset + realStride * y
      array.view.slice(i, i + area.size.x) foreach { c ⇒
        pixelWriter(Color(c))
      }
    }
    modified = true
  }

  def getARGB(array: Array[Int], offset: Int, stride: Int, area: RectI) = {
    Log(s"getARGB $offset, $stride, $area")
    preparePixelAccess()
    val realStride = if (stride == 0) size.x else stride
//    val buffer = this.buf.get
    for (y ← 0 until area.size.y) {
      nativeBuffer.position = ((y + area.y) * size.x + area.x) * format.bytesPerPixel
      var i = offset + realStride * y + area.x
      for (j ← 0 until area.size.x) {
        array(i + j) = pixelReader().argb
      }
    }
  }

  def write(source: Array[Byte], offset: Int, stride: Int, area: RectI) = {
//    val buffer = this.buf.get
    val bpp = format.bytesPerPixel
    val realStride = if (stride == 0) size.x * bpp else stride
    assert(bpp > 0)
    //    Log(s"Texture write: $offset, $stride, $area, $bpp, ${source.length}, ${buffer.limit()}")
    for (iy ← 0 until area.size.y) {
      nativeBuffer.position = ((iy + area.y) * size.x + area.x) * bpp
      ???
//      buffer.put(source, offset + realStride * iy, area.size.x * bpp)
    }
    modified = true

  }

  def read(target: Array[Byte], offset: Int = 0, stride: Int = 0, area: RectI = this.area) = {
    preparePixelAccess()
    ???
  }

  // override def encode(target: DataBuffer.Write, fileFormat: Texture.FileFormat, flags: Int): Unit = {
  //   assert(format == Format.RGBA_8888, "Texture format not supported")
  //   assert(fileFormat == FileFormat.PNG, "File format not supported")
  //   preparePixelAccess()
  //   val out = new ByteArrayOutputStream()
  //   ??? // JoglUtil.encodePNG(this, out)
  //   val result = out.toByteArray()
  //   target.write(result, 0, result.length)

  // }
}
