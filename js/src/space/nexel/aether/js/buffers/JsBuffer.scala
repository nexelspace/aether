package space.nexel.aether.js.buffers

import space.nexel.aether.core.buffers.NativeBuffer
import org.scalajs.dom.webgl.{RenderingContext => GL}
import space.nexel.aether.core.types.Num

import scala.scalajs.js.typedarray.Float32Array
import scala.scalajs.js.typedarray.Int16Array
import scala.scalajs.js.typedarray.Int32Array
import scala.scalajs.js.typedarray.Int8Array
import scala.scalajs.js.typedarray.TypedArray
import scala.scalajs.js.typedarray.Uint16Array
import scala.scalajs.js.typedarray.Uint32Array
import scala.scalajs.js.typedarray.Uint8Array
import scala.scalajs.js.typedarray.Uint8ClampedArray

object JsBuffer {
  def create(t: Num, size: Int) = t match {
    case Num.Byte   => new Int8Buffer(size)
    case Num.UByte  => new Uint8Buffer(size)
    case Num.Short  => new Int16Buffer(size)
    case Num.UShort => new Uint16Buffer(size)
    case Num.Int    => new Int32Buffer(size)
    case Num.UInt   => new Uint32Buffer(size)
    case Num.Float  => new Float32Buffer(size)
    case dt           => sys.error(s"Unsupported buffer datatype $dt")
  }
}

/**
  * Native data buffer.
  */
abstract class JsBuffer[T](var array: TypedArray[T, _]) extends NativeBuffer {

  //TODO
  def glType: Int

  protected var lim: Int = capacity
  protected var pos: Int = _

  def remaining: Int = lim - pos
  def capacity: Int = array.length
  def limit: Int = lim
  def position: Int = pos
  def position_=(newPosition: Int) = pos = newPosition
  def flip() = {
    pos = 0;
    lim = pos
  }

  def clear() = {
    pos = 0;
    lim = array.length
  }

  def compact() = {
    assert(pos >= 0 && pos <= lim && lim <= array.length, "Buffer corrupt: " + this)
    System.arraycopy(array, pos, array, 0, lim - pos)
    pos = lim - pos
    lim = array.length
  }

  //  def toArray: Array[_]
  //
  //  override def toString: String = {
  //    val array = toArray
  //    val contents = if (array.length > 8) array.take(8).mkString(", ") + ", ..]"
  //    else array.mkString(", ")
  //    s"[$position:$size:$contents]"
  //  }

  // --

  def get(): T = {
    assert(pos < lim, "Buffer overflow")
    pos += 1
    array(pos - 1)
  }

  def put(value: T) = {
    //    ensureSize(pos + 1)
    array(pos) = value
    pos += 1
  }

  def resizeBuffer(size: Int) = {
    val next = newArray(size)
    next.asInstanceOf[Uint8ClampedArray].set(array.asInstanceOf[Uint8ClampedArray])
    array = next
    lim = capacity
  }

  def newArray(size: Int): TypedArray[T, _]

  // --
  def arrayBuffer = array.buffer

  override def printElement(index: Int): String = array.get(index).toString

}

class Uint8ClampedBuffer(array: Uint8ClampedArray) extends JsBuffer(array) {

  val glType = GL.UNSIGNED_BYTE

  override def getI(): Int = get()
  override def getF(): Float = get()
  override def putI(v: Int) = put(v.toByte)
  override def putF(v: Float) = put(v.toByte)

  //  def createArray(size: Int) = new Uint8ClampedArray(size)

  def newArray(size: Int) = new Uint8ClampedArray(size)

}

class Int8Buffer(size: Int) extends JsBuffer(new Int8Array(size)) {
  val glType = GL.BYTE

  override def getI(): Int = get()
  override def getF(): Float = get()
  override def putI(v: Int) = put(v.toByte)
  override def putF(v: Float) = put(v.toByte)

  //  def toArray: Array[Byte] = (for (i <- 0 until buffer.length) yield buffer(i).toByte).toArray

  def newArray(size: Int) = new Int8Array(size)
}

class Uint8Buffer(size: Int) extends JsBuffer(new Uint8Array(size)) {

  val glType = GL.UNSIGNED_BYTE

  override def getI(): Int = get()
  override def getF(): Float = get()
  override def putI(v: Int) = put(v.toByte)
  override def putF(v: Float) = put(v.toByte)

  //TODO: test these
  override def writeB(value: Int): Unit = put(value.toByte)
  override def writeS(value: Int): Unit = { writeB(value >> 8); writeB(value) }
  override def writeI(value: Int): Unit = { writeS(value >> 16); writeS(value) }
  override def writeL(value: Long): Unit = { writeI((value >> 32).toInt); writeI(value.toInt) }

  def newArray(size: Int) = new Uint8Array(size)

}

class Int16Buffer(size: Int) extends JsBuffer(new Int16Array(size)) {
  val glType = GL.SHORT

  override def getI(): Int = get()
  override def getF(): Float = get()
  override def putI(v: Int) = put(v.toShort)
  override def putF(v: Float) = put(v.toShort)

  def newArray(size: Int) = new Int16Array(size)
}

class Uint16Buffer(size: Int) extends JsBuffer(new Uint16Array(size)) {
  val glType = GL.UNSIGNED_SHORT

  override def getI(): Int = get()
  override def getF(): Float = get()
  override def putI(v: Int) = put(v.toShort)
  override def putF(v: Float) = put(v.toShort)

  def newArray(size: Int) = new Uint16Array(size)
}

class Int32Buffer(size: Int) extends JsBuffer(new Int32Array(size)) {
  val glType = GL.INT

  override def getI(): Int = get()
  override def getF(): Float = get()
  override def putI(v: Int) = put(v.toInt)
  override def putF(v: Float) = put(v.toInt)

  def newArray(size: Int) = new Int32Array(size)
}

class Uint32Buffer(size: Int) extends JsBuffer(new Uint32Array(size)) {
  val glType = GL.UNSIGNED_INT

  override def getI(): Int = get().toInt
  override def getF(): Float = get().toFloat
  override def putI(v: Int) = put(v.toDouble)
  override def putF(v: Float) = put(v.toDouble)

  def newArray(size: Int) = new Uint32Array(size)

  //TODO: add all read/write operations
  override def writeI(value: Int): Unit = put(value.toDouble)
}

class Float32Buffer(size: Int) extends JsBuffer(new Float32Array(size)) {
  val glType = GL.FLOAT

  override def getI(): Int = get().toInt
  override def getF(): Float = get()
  override def putI(v: Int) = put(v.toFloat)
  override def putF(v: Float) = put(v.toFloat)

  def newArray(size: Int) = new Float32Array(size)
}
