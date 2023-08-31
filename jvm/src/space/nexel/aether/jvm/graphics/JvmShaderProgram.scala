package space.nexel.aether.jvm.graphics

import space.nexel.aether.core.graphics.*
import space.nexel.aether.core.platform.*
import space.nexel.aether.core.buffers.*
import space.nexel.aether.core.graphics.ShaderProgram.Config
import space.nexel.aether.core.graphics.ShaderProgram.ShaderProgramFactory

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL13._
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._
import org.lwjgl.opengl.GL31._
import org.lwjgl.opengl.GL41._
import org.lwjgl.opengl.GL43._

import java.nio.IntBuffer
import scala.collection.mutable

object JvmShaderProgram {
  val factory = new Resource.Factory[ShaderProgram, ShaderProgram.Config] {
    given ShaderProgramFactory = this
    def createThis(config: Config) = new JvmShaderProgram(config)
  }

}

class JvmShaderProgram(config: Config)(using factory: ShaderProgramFactory) extends ShaderProgram {
  val vertexShader = config.vertexShader.asInstanceOf[JvmShaderObject]
  val fragmentShader = config.fragmentShader.asInstanceOf[JvmShaderObject]

  def bindAttribute(index: Int, attributeName: String): Unit = ???

  def error: Option[String] = _error
  var _error: Option[String] = None

  val glProgram = glCreateProgram()
  val ENCODING = "UTF-8"

  glAttachShader(glProgram, vertexShader.glShader)
  glAttachShader(glProgram, fragmentShader.glShader)

  val errorBuffer = new StringBuffer()
  var attributeMap: Map[Int, ShaderAttribute] = _
  var attribNames: Map[String, ShaderAttribute] = _
  var uniformMap: Map[String, ShaderUniform] = _
  var uniformArrays: Map[String, IndexedSeq[ShaderUniform]] = _

  link()

  def attributes: Iterable[String] = attributeMap.values.map(_.name)
  def uniforms: Iterable[String] = uniformMap.keys

  def release() = {
    factory.released(this)
    glDetachShader(glProgram, vertexShader.glShader)
    glDetachShader(glProgram, fragmentShader.glShader)
    glDeleteProgram(glProgram)
  }

  def link() = {

    def check(parameter: Int, operation: String) = {
      if (glGetProgrami(glProgram, parameter) == GL_FALSE) {
        errorBuffer.append(s"Shader program $operation failed:\n")
        val log = glGetProgramInfoLog(glProgram)
        Log("Info: " + log)
        errorBuffer.append(log)
        //assert(false, errorBuffer)
        _error = Some(errorBuffer.toString())
        throw new RuntimeException("Failed to link shader\n" + errorBuffer)
      }

    }

    glLinkProgram(glProgram)
    check(GL_LINK_STATUS, "link")
    glValidateProgram(glProgram)
    check(GL_VALIDATE_STATUS, "validate")
    val attLen = glGetProgrami(glProgram, GL_ACTIVE_ATTRIBUTE_MAX_LENGTH)
    val uniLen = glGetProgrami(glProgram, GL_ACTIVE_UNIFORM_MAX_LENGTH)

    val attributeCount = glGetProgrami(glProgram, GL_ACTIVE_ATTRIBUTES)
    // Log("Attributes in shader program " + glProgram + ": " + attributeCount)
    val attribs = mutable.Map[Int, ShaderAttribute]()
    for (i <- 0 until attributeCount) {
      val size = BufferUtils.createIntBuffer(1)
      val typ = BufferUtils.createIntBuffer(1)
      val name = glGetActiveAttrib(glProgram, i, size, typ)
      val location = glGetAttribLocation(glProgram, name)
      val attribute = new ShaderAttribute(location, name, size.get(0), typ.get(0))
      attribs.put(location, attribute)
    }
    attributeMap = attribs.toMap
    attribNames = attributeMap.values.map(a => a.name -> a).toMap
    // find uniforms
    val uniformCount = glGetProgrami(glProgram, GL_ACTIVE_UNIFORMS);
    // Log("Uniforms in shader program " + glProgram + ": " + uniformCount);
    val unis = mutable.Map[String, ShaderUniform]()
    for (i <- 0 until uniformCount) {
      val size = BufferUtils.createIntBuffer(1)
      val typ = BufferUtils.createIntBuffer(1)
      val name = glGetActiveUniform(glProgram, i, 256, size, typ)
      val location = glGetUniformLocation(glProgram, name)
      assert(location >= 0)
      val arrayLength = glGetActiveUniformsi(glProgram, location, GL_UNIFORM_SIZE)
      val uniform = new ShaderUniform(name, glProgram, location, size.get(0), typ.get(0), arrayLength)
      unis.put(name, uniform)
      //Log("  Uniform " + i + ", " + name);
    }
    uniformMap = unis.toMap
  }

  def textureUnit(textureUnit: Int, texture: Texture) = {
    texture.asInstanceOf[JvmTexture].bind(textureUnit)
  }

  override def hasUniform(name: String): Boolean = uniformMap.contains(name)

  override def uniform(name: String): Option[Var] = uniformMap.get(name)

  override def attribute(name: String): Option[Var] = attribute(attributeIndex(name))

  override def hasAttribute(name: String): Boolean = attribNames.contains(name)

  override def attribute(index: Int): Option[Var] = {
    glDisableVertexAttribArray(index)
    val attribute = attributeMap(index).asInstanceOf[ShaderAttribute]
    if (attribute == null) {
      return None
    }
    assert(attribute.index == index)
    //    assert(index > 0, "On some environments attribute index 0 can't be disabled")
    attribute.debugSize = -1
    Some(attribute)
  }

  def attributeIndex(name: String) = {
    assert(attribNames.contains(name), "No attribute " + name + " in " + attributes)
    attribNames(name).index
  }

  def attributeBuffer(name: String, buffer: ShaderBuffer, numComponents: Int) = {
    val index = attributeIndex(name)
    attributeBuffer(index, buffer, numComponents)
    attributeMap(index).value = buffer.toString()
  }

  def attributeBuffer(index: Int, buffer: ShaderBuffer, numComponents: Int) = {
    val buf = buffer.asInstanceOf[JvmShaderBuffer]
    buf.prepareRender()
    glEnableVertexAttribArray(index)
    glVertexAttribPointer(index, numComponents, buf.glType, buf.normalized, 0, 0)
    val attribute = attributeMap(index)
    attribute.value = buffer.toString()
    attribute.debugSize = {
      //      assert(buf.buffer.remaining() % buf.numComponents == 0)
      buf.remaining / numComponents
    }
  }

  def storageBuffer(index: Int, buffer: ShaderBuffer) = {
    val buf = buffer.asInstanceOf[JvmShaderBuffer]
    buf.prepareRender()
    assert(buffer.target == ShaderBuffer.Target.Storage)
    glBindBufferBase(GL_SHADER_STORAGE_BUFFER, index, buf.glBufferId)
  }

  def textureBuffer(textureUnit: Int, buffer: ShaderBuffer) = {
    assert(buffer.target == ShaderBuffer.Target.Texture)
    val buf = buffer.asInstanceOf[JvmShaderBuffer]
    buf.prepareRender()
    glActiveTexture(GL_TEXTURE0 + textureUnit)
    glBindTexture(GL_TEXTURE_BUFFER, buf.glTextureId.get)
    val format = GL_R32UI //TODO
    glTexBuffer(GL_TEXTURE_BUFFER, format, buf.glBufferId)
  }

  import ShaderProgram.Mode._

  def glMode(mode: ShaderProgram.Mode) = mode match {
    case Points        => GL_POINTS
    case LineStrip     => GL_LINE_STRIP
    case LineLoop      => GL_LINE_LOOP
    case Lines         => GL_LINES
    case TriangleStrip => GL_TRIANGLE_STRIP
    case TriangleFan   => GL_TRIANGLE_FAN
    case Triangles     => GL_TRIANGLES
  }

  def draw(mode: ShaderProgram.Mode, index: Int, length: Int) = {
    for (attrib <- attributeMap.values) {
      if (attrib.debugSize >= 0) {
        assert(attrib.debugSize == length, s"Invalid attribute buffer size in ${attrib.name}: ${attrib.debugSize}, $length")
      }
    }

    glUseProgram(glProgram)
    glDrawArrays(glMode(mode), index, length)
  }

  def draw(indexBuffer: ShaderBuffer, mode: ShaderProgram.Mode, index: Int, length: Int) = {
    val buffer = indexBuffer.asInstanceOf[JvmShaderBuffer]
    glUseProgram(glProgram)
    buffer.prepareRender()
    glDrawElements(glMode(mode), length, buffer.glType, index)
  }

}
