package aether.lib.font

import aether.core.types.Vec2I

import scala.collection.mutable.ListBuffer
import aether.core.types.RectI
import aether.core.graphics.Texture

object FontParser {

  val down = Vec2I(0, 1)
  val right = Vec2I(1, 0)

  class OutOfBounds extends Exception

  case class Region(logical: RectI, actual: RectI) {
    override def toString = s"{$logical $actual}"
  }

  def parseRegions(source: Texture): List[Region] = {
    val area = source.area

    def findControl(start: Vec2I, offset: Vec2I): Vec2I = {
      val begin = control(source.getARGB(start.x, start.y))
      var p = start
      while {
        p += offset
        if (!area.isInside(p)) throw new OutOfBounds()
        control(source.getARGB(p.x, p.y)) == begin
      } do {}
      p
    }

    val top = findControl(Vec2I(0, 0), down)
    val bottom = findControl(top, down)
    val controlLine = findControl(bottom, down)

    val result = ListBuffer[Region]()
    var next = controlLine + right
    try {
      while (true) {
        val begin = findControl(next, right)
        val end = findControl(begin, right)
        val logical = RectI(begin.x, top.y, end.x - begin.x, bottom.y - top.y)
        val actual = adjust(source, logical)
        result += new Region(logical, actual)
        next = end
      }
    } catch {
      case _: OutOfBounds => //Log("Font parser OutOfBounds")
    }

    result.toList

  }

  def control(argb: Int): Boolean = {
    val a = argb >>> 24
    if (a > 64 && a < 192) throw new IllegalArgumentException("Unexpected control alpha: " + a)
    a > 128
  }

  def adjust(source: Texture, area: RectI): RectI = {
    //TODO
    area
  }

}
