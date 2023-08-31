package space.nexel.aether.core.graphics

import space.nexel.aether.core.platform.Resource
import space.nexel.aether.core.types.Vec2I
import space.nexel.aether.core.platform.NativeResource
import space.nexel.aether.core.types.RectI
import space.nexel.aether.core.types.Num
import space.nexel.aether.core.buffers.NativeBuffer
import space.nexel.aether.core.internal.FlagFactory
import space.nexel.aether.core.internal.FlagOps
import space.nexel.aether.core.graphics.Graphics.RenderTarget

object Texture {
  type TextureFactory = Resource.Factory[Texture, Config]

  enum Format(val bitsPerPixel: Int, val hasAlpha: Boolean = false) {

    def bytesPerPixel = ((bitsPerPixel - 1) >> 3) + 1

    def componentType: Num = this match {
      case Format.RGBA_8888    => Num.UByte
      case Format.RGBA_8888_UI => Num.UByte
      case Format.R32UI        => Num.UInt
      case format              => scala.sys.error(s"TODO: determine buffer type for $format")
    }

    case UNDEFINED extends Format(0)
    case PVRTC_2BIT extends Format(2)
    case PVRTC_2BIT_RGB extends Format(2)
    case DXT1 extends Format(4)
    case ETC1 extends Format(4)
    case PVRTC_4BIT extends Format(4)
    case PVRTC_4BIT_RGB extends Format(4)
    case LUMINANCE_8 extends Format(8)
    case DXT3 extends Format(8)
    case DXT5 extends Format(8)
    case RGB_565 extends Format(16)
    case RGB_1555 extends Format(16)
    case RGBA_4444 extends Format(16)
    case RGBA_5551 extends Format(16)
    case DEPTH_16 extends Format(16)
    case LUMINANCE_16 extends Format(16)
    case R_16 extends Format(16)
    case RGB_888 extends Format(24)
    case BGR_888 extends Format(24)
    case SRGB_888 extends Format(24)
    case DEPTH_24 extends Format(24)
    case ARGB_8888 extends Format(32, true)
    case RGBA_8888 extends Format(32, true)
    case RGBA_8888_UI extends Format(32, true)
    case RGB_8888 extends Format(32)
    case BGRA_8888 extends Format(32, true)
    case ABGR_8888 extends Format(32, true)
    case DEPTH_32 extends Format(32)
    case DEPTH_32F extends Format(32)
    case RG_16 extends Format(32)
    case R32UI extends Format(32)

  }

  enum FileFormat {
    case UNDEFINED, RAW, PNG, JPEG, CRN, DDS, PVR, PKM
  }

  object TextureFlag extends FlagFactory[TextureFlag](new TextureFlag(_), _.flag)
  class TextureFlag(val flag: Int) extends AnyVal with FlagOps

  object Flag {
    val Color, Alpha, RenderTarget, Writable, Readable, NotRenderSource, Mipmap, Async, Premultiplied, Cubemap =
      TextureFlag.shift(12, 0xfff000)
  }
  // type Format = TextureFlag
  // type Flag = TextureFlag

  case class Config(
      id: Int = 0,
      flags: TextureFlag = 0,
      format: Format = Format.RGBA_8888,
      fileFormat: FileFormat = FileFormat.UNDEFINED,
      size: Option[Vec2I] = None,
      argb: Option[Array[Int]] = None,
      data: Option[Array[Byte]] = None,
      buffer: Option[NativeBuffer] = None
  ) extends Resource.Config

  def create(sizeX: Int, sizeY: Int)(using graphics: Graphics) = {
    graphics.textureFactory.create(Config(size = Some(Vec2I(sizeX, sizeY))))
  }
}

trait Texture extends NativeResource[Texture, Texture.Config] with RenderTarget {
  val config: Texture.Config
  val size = config.size.get

  def area = RectI(0, 0, size.x, size.y)

  def format: Texture.Format

  /** Direct buffer access. */
  def buffer: Option[NativeBuffer]

  def getARGB(x: Int, y: Int): Int

  def setARGB(argb: Int, x: Int, y: Int): Unit

  def setARGB(array: Array[Int], offset: Int = 0, stride: Int = 0, area: RectI = this.area): Unit

  /** Get image data in ARGB format.
    * @offset
    *   Offset for array data.
    * @stride
    *   Pixel stride between rows, 0 for default stride .
    */
  def getARGB(array: Array[Int], offset: Int = 0, stride: Int = 0, area: RectI = this.area): Unit

  def write(source: Array[Byte], offset: Int = 0, stride: Int = 0, area: RectI = this.area): Unit

  def read(target: Array[Byte], offset: Int = 0, stride: Int = 0, area: RectI = this.area): Unit

  // def encode(target: DataBuffer.Write, format: Texture.FileFormat, flags: Int): Unit

  def isPremultiplied = config.flags.has(Texture.Flag.Premultiplied)
  def isRenderTarget = config.flags.has(Texture.Flag.RenderTarget)
  val isReadable = config.flags.has(Texture.Flag.Readable)
  val isWritable = config.flags.has(Texture.Flag.Writable)
  val hasMipmaps = config.flags.has(Texture.Flag.Mipmap)

  // -----


  /** Texture internal buffer has been modified and needs to be updated to actual texture. */
  protected var modified = false
   /** Texture has been modified by rendering, internal buffer is out of sync. */
  protected var rendered = false

  protected def nativeBuffer = buffer.get

  lazy val pixelWriter = TextureOps.argbWriter(nativeBuffer, format, isPremultiplied)
  lazy val pixelReader = TextureOps.argbReader(nativeBuffer, format)

  def bufferModified() = {
    modified = true
  }

  def prepareRenderTarget() = {
    prepareRender()
    rendered = true
  }

  protected def prepareRender() = {
    synchronized {
      if (modified) {
        modified = false
        nativeBuffer.position = 0
        upload()
      }
    }
  }

  def upload(): Unit
}
