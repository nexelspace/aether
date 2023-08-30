package aether.jvm.lwjgl

import aether.core.platform.shader.ShaderBuffer
import aether.core.platform.shader.ShaderBuffer.Config
import aether.core.platform.shader.ShaderBuffer.Flag
import aether.core.platform.shader.ShaderBuffer.Target
import aether.core.platform.shader.ShaderBuffer.Type
import space.nexel.aether.core.buffers.BufferWrapper
import aether.core.platform.internal.NativeFactory
import aether.core.util.Strings
import aether.jvm.buffers.JvmBuffer
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL13._
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._
import org.lwjgl.opengl.GL31._
import org.lwjgl.opengl.GL41._
import org.lwjgl.opengl.GL43._

import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

object NShaderBuffer extends NativeFactory[ShaderBuffer, ShaderBuffer.Config](true) {

  val array = new Array[Float](16)

  override def createThis(config: ShaderBuffer.Config): ShaderBuffer = {
    new NShaderBuffer(config)
  }
}

class NShaderBuffer(config: Config) extends ShaderBuffer(config) with BufferWrapper {
 NRenderer.assertInitialized()

  override val dataType = config.dataType
  override val target = config.flags & Target.Mask

  val buffer: JvmBuffer = JvmBuffer.create(config.dataType, config.capacity)

  override def putI(v: Int): Unit = buffer.putI(v)
  override def putF(v: Float): Unit = buffer.putF(v)

  override def getI(): Int = buffer.getI()
  override def getF(): Float = buffer.getF()
  //  override def getD(): Double = buffer.getD()

  override def resizeBuffer(size: Int): Unit = {
   assert(config.dynamic)
   buffer.resizeBuffer(size)
 }

  //TODO
  override def size = (if (readMode) buffer.buffer.limit() else buffer.buffer.position())

  override def clear() = {
    buffer.clear()
    readMode = false
  }

  val normalized = (config.flags & Flag.Normalize) != 0

  val result = new Array[Int](1)
  glGenBuffers(result)
  val glBufferId = result(0)
  assert(glBufferId > 0, "glGenBuffers failed: " + glGetError() + ", " + Strings.toHex(glGetError(), 4))

  var readMode = false
  //  var buffer: Buffer = null

  // Texture Buffer support
  val glTextureId = if (target == Target.Texture) {
    Some(glGenTextures())
  } else {
    None
  }
  
  val glTarget = target match {
    case Target.Vertex  => GL_ARRAY_BUFFER
    case Target.Index   => GL_ELEMENT_ARRAY_BUFFER
    case Target.Uniform => GL_UNIFORM_BUFFER
    case Target.Storage => GL_SHADER_STORAGE_BUFFER
    case Target.Texture => GL_TEXTURE_BUFFER
  }

  val glType = config.flags & Type.Mask match {
    case Type.Byte   => GL_BYTE
    case Type.UByte  => GL_UNSIGNED_BYTE
    case Type.Short  => GL_SHORT
    case Type.UShort => GL_UNSIGNED_SHORT
    case Type.Int    => GL_INT
    case Type.UInt   => GL_UNSIGNED_INT
    case Type.Float  => GL_FLOAT
  }

  def releaseThis() = {
    //SystemObject.destroy(this, objects)
    glDeleteBuffers(Array(glBufferId))
  }

  def prepareRender() = {
    glBindBuffer(glTarget, glBufferId)
    if (!readMode) {
      readMode = true
      buffer.flip()
      buffer.buffer match {
        case b: ByteBuffer => glBufferData(glTarget, b, GL_DYNAMIC_DRAW)
        case b: IntBuffer => glBufferData(glTarget, b, GL_DYNAMIC_DRAW)
        case b: FloatBuffer => glBufferData(glTarget, b, GL_DYNAMIC_DRAW)
        case b =>
          val t = b.getClass.getName
          sys.error(s"Buffer type $t not implemented")
      }
    }
  }

  //def size() = components*buffer.remaining()

  override def toString(): String = {
    s"[$dataType-${config.flags.toHexString}, $buffer]"
  }
}