package space.nexel.aether.core.graphics

import ShaderBuffer._
import space.nexel.aether.core.types.Num
import space.nexel.aether.core.platform.*
import space.nexel.aether.core.buffers.ElementBuffer

/**
  * Shader data buffer.
  */
object ShaderBuffer {
  type ShaderBufferFactory = Resource.Factory[ShaderBuffer, ShaderBuffer.Config]

//TODO
//  sealed abstract class Target
//  object Target {
//    case object Mask extends Target
//    case object Vertex extends Target
//    case object Index extends Target
//    case object Uniform extends Target
//    case object Storage extends Target
//    case object Texture extends Target
//  }
    
  object Type {
    val Mask = 0x0f

    val Byte = 0x01
    val UByte = 0x02
    val Short = 0x03
    val UShort = 0x04
    val Int = 0x05
    val UInt = 0x06
    val Float = 0x08
    val HalfFloat = 0x09
    val Double = 0x0a
  }

  object Size {
    val Mask = 0xf0
    /** Buffer grows dynamically. */
    val Dynamic = 0x10
    /** Buffer is fixed size. */
    val Static = 0x20
  }

  object Target {
    val Mask = 0xf00
    val Vertex = 0x100
    val Index = 0x200
    val Uniform = 0x300
    val Storage = 0x400
    val Texture = 0x500
  }

  object Hint {
    val Mask = 0xf000
    /** Buffer content is modified frequently. */
    val Dynamic = 0x1000
    /** Buffer content is modified infrequently. */
    val Static = 0x2000
  }

  object Flag {
    val Normalize = 0x10000
  }

  def dataType(flags: Int): Num = (flags & Type.Mask) match {
    case Type.Byte => Num.Byte
    case Type.UByte => Num.UByte
    case Type.Short => Num.Short
    case Type.UShort => Num.UShort
    case Type.Int => Num.Int
    case Type.UInt => Num.UInt
    case Type.Float => Num.Float
  }

  val defaultCapacity = 16
  def apply(flags: Int, capacity: Int)(using graphics: Graphics): ShaderBuffer = {
    assert((flags & Size.Dynamic)!=0 || capacity>0)
    val cap = if (capacity==0) defaultCapacity else capacity
    graphics.shaderBufferFactory(Config(flags, cap))
  }

  case class Config(flags: Int, capacity: Int) extends Resource.Config {
    val dataType: Num = ShaderBuffer.dataType(flags)
    //def target = flags & Target.Mask
    def dynamic = (flags & Size.Dynamic)!=0
  }
}

/**
 * Generic shader buffer object.
 * 
 * ShaderBuffer uses Buffer as underlying buffer interface.
 */
trait ShaderBuffer(val config: Config)
    extends NativeResource[ShaderBuffer, ShaderBuffer.Config] with ElementBuffer {

  /** Element data type. */
  val dataType: Num
  
  val target: Int

  /** Number of elements in buffer. */
  def size: Int = position

  /** Total size in bytes. */
  //final def bytes: Int = size * dataType.bytes

  def resizeBuffer(size: Int): Unit

  // -- ElementIO

  def getI(): Int
  def getF(): Float
  def putI(value: Int): Unit
  def putF(value: Float): Unit

  //TODO: implement Double buffer
  def getD(): Double = getF()
  def putD(value: Double) = putF(value.toFloat)

}
