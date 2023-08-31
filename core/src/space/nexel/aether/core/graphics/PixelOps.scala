package space.nexel.aether.core.graphics

import space.nexel.aether.core.types.Vec2I
import space.nexel.aether.core.buffers.NativeBuffer
import space.nexel.aether.core.types.Color
import space.nexel.aether.core.types.RectI
import space.nexel.aether.core.platform.Log

trait PixelOps {

  def size: Vec2I
  def format: Texture.Format
  def nativeBuffer: NativeBuffer
  def isPremultiplied: Boolean

  def preparePixelAccess(): Unit
  def upload(): Unit

  /** Texture internal buffer has been modified and needs to be updated to actual texture. */
  protected var modified = false
  /** Texture has been modified by rendering, internal buffer is out of sync. */
  protected var rendered = false

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

  def getARGB(x: Int, y: Int): Int = {
    preparePixelAccess()
    val inside = x >= 0 && x < size.x && y >= 0 && y < size.y
    // assert(inside, s"Invalid coords $x: $size.x, $y: $size.y")
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

  // def setARGB(argb: Int, x: Int, y: Int): Unit
  final def setARGB(argb: Int, x: Int, y: Int) = {
    assert(x >= 0 && x < size.x && y >= 0 && y < size.y, s"Out of bounds $x, $y in $size.x, $size.y")
    preparePixelAccess()
    val index = (y * size.x + x) * format.bytesPerPixel
    assert(index >= 0 && index < nativeBuffer.limit, s"Invalid index $index, $nativeBuffer")
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

  /** Get image data in ARGB format.
    * @offset
    *   Offset for array data.
    * @stride
    *   Pixel stride between rows, 0 for default stride .
    */
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

  def read(target: Array[Byte], offset: Int = 0, stride: Int = 0, area: RectI = RectI(Vec2I.Zero, size)) = {
    preparePixelAccess()
    ???
  }
}
