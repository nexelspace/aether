package space.nexel.aether.js.graphics

import space.nexel.aether.js.buffers.JsBuffer
import org.scalajs.dom.raw.{WebGLRenderingContext => GL}
import org.scalajs.dom.Image
import space.nexel.aether.core.math.VMathI
import space.nexel.aether.core.platform.Resource
import space.nexel.aether.core.platform.Log
import space.nexel.aether.core.platform.Dispatcher
import space.nexel.aether.core.graphics.*
import space.nexel.aether.core.graphics.Texture.*
import space.nexel.aether.core.types.*
import space.nexel.aether.core.util.*
import space.nexel.aether.core.base.Ref
import org.scalajs.dom
import org.scalajs.dom.HTMLImageElement
import space.nexel.aether.core.buffers.NativeBuffer
import org.scalajs.dom.raw.HTMLCanvasElement
import space.nexel.aether.core.buffers.DataBuffer

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.Promise
import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.scalajs.js.typedarray.TypedArray
import scala.scalajs.js.typedarray.Uint8Array
import scala.scalajs.js.typedarray.Uint8ClampedArray

object JsTexture {

  def nextPOT(v: Int) = 1 << (VMathI.log2(v - 1) + 1)
  def isPOT(size: Vec2I) = Bits.ones(size.x) == 1 && Bits.ones(size.y) == 1

  lazy val copyCanvas = dom.document.createElement("canvas").asInstanceOf[HTMLCanvasElement]

  def factory(g: Graphics) = new TextureFactory {
    given Graphics = g
    given TextureFactory = this

    override def loadThis(ref: Ref, config: Config)(using dispatcher: Dispatcher) = {
      val url = ref.toUrl
      val resource = new Resource[Texture]()
      val image = new Image()
      image.src = url
      // image.onload = { event =>
      image.addEventListener("load", _ => load())
      image.addEventListener("error", _ => error())
      def load() = {
        Log(s"Image $url loaded")
        resource.set(npotTexture(config, image))
      }
      def error() = {
        resource.error = s"Failed to load image $url"
      }
      resource
    }

    def createThis(config: Config): Texture = {
      assert(config.size.isDefined, "Texture size is not defined")
      config.format match {
        case Format.RGBA_8888 | Format.R32UI | Format.RGBA_8888_UI =>
        case format                                                => sys.error(s"Unsupported texture format $format")
      }
      val pot = isPOT(config.size.get)
      val size = if (pot) {
        config.size.get
      } else {
        config.size.get(nextPOT)
      }
      val typ = config.format.componentType
      Log(s"Create buffer $size, ${config.format}, ${config.format.bytesPerPixel}, ${typ.bytes}")
      val buffer = JsBuffer.create(typ, config.format.bytesPerPixel / typ.bytes * size.x * size.y)

      // val length = sizeX * sizeY * 4
      val array = buffer.array.asInstanceOf[TypedArray[Byte, _]] // TODO

      config.argb.foreach { argb =>
        assert(argb.size == size.x * size.y)
        assert(pot, "NPOT resize not tested")
        val source = argb.iterator
        val p = (0 until config.size.get.x * config.size.get.y * 4).iterator
        while (p.hasNext) {
          val a = source.next
          array(p.next) = (a >>> 16).toByte // ARGB -> RGBA conversion
          array(p.next) = (a >>> 8).toByte
          array(p.next) = (a >>> 0).toByte
          array(p.next) = (a >>> 24).toByte
        }
      }

      val newConfig = config.copy(size = Some(size), buffer = Some(buffer))

      new JsTexture(newConfig)

    }

    def getData(image: HTMLImageElement): NativeBuffer = {
      getData(image, Vec2I(image.width, image.height))
    }

    def getData(image: HTMLImageElement, size: Vec2I): NativeBuffer = {
      copyCanvas.width = size.x
      copyCanvas.height = size.y
      val ctx = copyCanvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
      ctx.drawImage(image, 0, 0, image.width, image.height)
      val data = ctx.getImageData(0, 0, size.x, size.y)

      Log(s"Creating buffer for $image")
      val buffer = JsBuffer.create(Num.UByte, size.x * size.y * 4) // TODO size
      buffer.array.asInstanceOf[Uint8ClampedArray].set(data.data.asInstanceOf[Uint8ClampedArray])
      buffer
    }

    def npotTexture(config: Config, image: HTMLImageElement): Texture = {
      if (isPOT(Vec2I(image.width, image.height))) {
        new JsTexture(config.copy(size = Some(Vec2I(image.width, image.height)), buffer = Some(getData(image))))

      } else {
        val size = Vec2I(nextPOT(image.width), nextPOT(image.height))
        Log(s"Resize NPOT texture ${image.width} x ${image.height} -> $size")
        val newConfig = Config(size = Some(size), buffer = Some(getData(image, size)))
        new JsTexture(newConfig)
      }
    }
  }

}

class JsTexture(val config: Config)(using graphics: Graphics, factory: TextureFactory) extends Texture {

  override val format = config.format

  // def this(image: Image)(using factory: TextureFactory, gl: GL) = {
  //   this(Vec2I(image.width, image.height))
  // }
  val gl = graphics.asInstanceOf[JsGraphics].gl

  val glTexture = gl.createTexture()

  def isPowerOfTwo = JsTexture.isPOT(size)
  assert(isPowerOfTwo, s"NPOT textures are not supported. $config")
  Log(s"Create texture $size")

  def buffer: Option[JsBuffer[_]] = config.buffer.map(_.asInstanceOf[JsBuffer[_]])

  buffer.foreach { case buffer =>
    upload()
  }

  def upload() = {
    gl.bindTexture(GL.TEXTURE_2D, glTexture)
    texImage()
    gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_MAG_FILTER, GL.LINEAR)
    gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_MIN_FILTER, GL.LINEAR_MIPMAP_NEAREST)
    // if (isPowerOfTwo) gl.generateMipmap(GL.TEXTURE_2D)
    gl.bindTexture(GL.TEXTURE_2D, null)
  }

  def texImage() = {
    // TODO: support direct image without buffer
    //    NRenderer.gl.texImage2D(GL.TEXTURE_2D, 0, glType, glType, GL.UNSIGNED_BYTE, image)
    val b = nativeBuffer.asInstanceOf[JsBuffer[_]]
    val glInternalFormat = GlUtil.toGlInternalFormat(format)
    val glFormat = GlUtil.toGlFormat(format)

    val glType = b.glType
    // Log(s"texImage2D($glInternalFormat, $sizeX, $sizeY, $glFormat, $glType, ${b.array.size})")
    gl.texImage2D(GL.TEXTURE_2D, 0, glInternalFormat, size.x, size.y, 0, glFormat, glType, b.array)

  }

  def bind(textureIndex: Int) = {
    prepareRender()
    gl.activeTexture(GL.TEXTURE0 + textureIndex)
    gl.bindTexture(GL.TEXTURE_2D, glTexture)
    val filter = graphics.filter
    if (filter == Graphics.Filter.Linear) {
      gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_MAG_FILTER, GL.LINEAR)
      gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_MIN_FILTER, if (hasMipmaps) GL.LINEAR_MIPMAP_LINEAR else GL.LINEAR)
    } else {
      gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_MIN_FILTER, GL.NEAREST)
      gl.texParameteri(GL.TEXTURE_2D, GL.TEXTURE_MAG_FILTER, GL.NEAREST)
    }
  }

  val fb = gl.createFramebuffer()

  def preparePixelAccess() = {
    assert(isReadable || isWritable, "Texture has no pixel access")
    if (rendered) {
      rendered = false
      val buffer = nativeBuffer.asInstanceOf[JsBuffer[_]]

      gl.bindFramebuffer(GL.FRAMEBUFFER, fb)
      gl.bindTexture(GL.TEXTURE_2D, glTexture)
      gl.framebufferTexture2D(GL.FRAMEBUFFER, GL.COLOR_ATTACHMENT0, GL.TEXTURE_2D, glTexture, 0)
      nativeBuffer.clear()
      gl.readPixels(0, 0, size.x, size.y, GlUtil.toGlPixelFormat(format), GL.UNSIGNED_BYTE, buffer.array)
    }
  }

  // ---- public interface

  def release() = {
    factory.released(this)
    gl.deleteTexture(glTexture)
  }

  // override def encode(target: DataBuffer.Write, format: Texture.FileFormat, flags: Int): Unit = ???

}
