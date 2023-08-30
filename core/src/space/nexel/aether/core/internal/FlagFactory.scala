package space.nexel.aether.core.internal

import scala.language.implicitConversions

trait FlagOps extends Any {
  def flag: Int
  def has(singleFlag: FlagOps): Boolean = (flag & singleFlag.flag) != 0
}

object FlagFactory {
  private var seriesBegin = 0
  private var seriesCurrent = 0

  def series(first: Int) = {
    if (seriesBegin == first) {
      seriesCurrent += 1
    } else {
      seriesBegin = first
      seriesCurrent = first
    }
    seriesCurrent
  }

  private var shiftBegin = 0
  private var shiftCurrent = 0

  def shift(first: Int) = {
    if (shiftBegin == first) {
      shiftCurrent += 1
    } else {
      shiftBegin = first
      shiftCurrent = first
    }
    1 << shiftCurrent
  }

}

abstract class FlagFactory[T](val create: (Int) => T, val get: (T) => Int) {
  //    def create(x: Int): T
  //    def get(flag: T): Int

  def apply(x: Int) = {
    create(x)
  }
  def apply(x: Int, mask: Int) = {
    assert((x & ~mask) == 0, s"Flag mask overflow: ${x.toHexString}, ${mask.toHexString}")
    create(x)
  }
  def series(x: Int, mask: Int) = apply(FlagFactory.series(x), mask)
  def shift(x: Int, mask: Int) = apply(FlagFactory.shift(x), mask)

    implicit def toInt(flag: T): Int = get(flag)
    implicit def intToFlag(flag: Int): T = create(flag)

}
