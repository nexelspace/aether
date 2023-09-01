package aether.core.buffers

/**
 * Buffer with element put/get support.
 * Put & get methods apply to single buffer element, value is clamped or cast to fit.
 */
object ElementBuffer {
  trait Get {
    def getB(): Byte = getI().toByte
    def getS(): Short = getI().toShort
    def getI(): Int
    def getF(): Float
    def getD(): Double
  }

  trait Put {
    def putB(v: Byte) = putI(v)
    def putS(v: Short) = putI(v)
    def putI(v: Int): Unit
    def putF(v: Float): Unit
    def putD(v: Double): Unit
  }

  trait IO extends Put with Get

}

trait ElementBuffer extends Buffer with ElementBuffer.IO {

}
