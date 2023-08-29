package space.nexel.aether.core.input

class IntEnum {
  private var begin = 0
  private var current = 0

  def enumSeries(first: Int): Int = {
    if (begin == first) {
      current += 1
    } else {
      begin = first
      current = first
    }
    current
  }

  def enumBits(first: Int): Int = {
    if (begin == first) {
      current += 1
    } else {
      begin = first
      current = first
    }
    1 << (current - 1)
  }

}