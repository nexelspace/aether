package space.nexel.aether.lib.font

import space.nexel.aether.core.types.Vec4I
import scala.concurrent.Future
import space.nexel.aether.core.graphics.Texture
import space.nexel.aether.core.types.RectI
import space.nexel.aether.core.platform.Log
import space.nexel.aether.core.base.Base
import space.nexel.aether.core.base.Ref
import space.nexel.aether.core.graphics.Graphics
import space.nexel.aether.core.platform.Resource
import space.nexel.aether.core.platform.Dispatcher
import space.nexel.aether.core.platform.Platform

object Font {
  def load(base: Base, textureFile: String, configFile: String)(using platform: Platform, dispatcher: Dispatcher): Resource[Font] = {
    load(base.ref(textureFile), base.ref(configFile))
  }

  def load(textureRef: Ref, configRef: Ref)(using platform: Platform, dispatcher: Dispatcher): Resource[Font] = {
    for {
      texture <- platform.textureFactory.load(textureRef, Texture.Config(flags = Texture.Flag.Readable))
      config <- configRef.loadString()
    } yield create(texture, config)
  }

  def create(texture: Texture, config: String): Font = {
    val regions = FontParser.parseRegions(texture)
    new Font(config, regions.map(_.logical), texture)
  }
}

class Font(val chars: String, val regions: List[RectI], val texture: Texture, val marginsLTRB: Vec4I = Vec4I(0)) {

  assert(chars.length() == regions.length, s"Font config mismatch ${chars.length}, ${regions.length}")

  val regs =
    if (marginsLTRB == Vec4I(0)) {
      regions
    } else {
      //TODO: make proper margin handling
      val m = marginsLTRB
      regions.map {
        case r =>
          RectI(r.x - m.x, r.y - m.y, r.sizeX + m.x + m.z, r.sizeY + m.y + m.w)
      }
    }
  val regionMap = chars.zip(regs).toMap

  val height = if (regionMap.contains('a')) logicalArea('a').sizeY else 0

  def nochar(char: Char) = {
    Log(s"Font chars: $chars, regions: $regions, texture $texture")
    sys.error(s"Font has no glyph for char '$char', ${char.toInt}, 0x${char.toInt.toHexString}")
  }

  def width(char: Char): Int = regionMap.getOrElse(char, nochar(char)).sizeX

  def width(string: String): Int = string.map(width(_)).sum

  def hasChar(c: Char) = regionMap.contains(c)

  def logicalArea(c: Char): RectI = regionMap(c)

}
