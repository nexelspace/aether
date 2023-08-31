package space.nexel.aether.js.graphics

import org.scalajs.dom.raw.{WebGLRenderingContext => GL}
import space.nexel.aether.core.platform.*
import space.nexel.aether.core.graphics.ShaderProgram.*
import space.nexel.aether.core.graphics.*
import space.nexel.aether.core.buffers.*
import scala.collection.mutable

object JsShaderProgram {
  def factory(using gl: GL) = new ShaderProgramFactory {
    given ShaderProgramFactory = this
    def createThis(config: Config) = new JsShaderProgram(
      config.vertexShader.asInstanceOf[JsShaderObject],
      config.fragmentShader.asInstanceOf[JsShaderObject]
    )

  }

}

class JsShaderProgram(vertexShader: JsShaderObject, fragmentShader: JsShaderObject)(using
    factory: ShaderProgramFactory,
    gl: GL
) extends ShaderProgram {

  // --
  def bindAttribute(index: Int, attributeName: String): Unit = assert(false, "bindAttribute not implemented")

  def error: Option[String] = ???

  val glProgram = gl.createProgram()

  gl.attachShader(glProgram, vertexShader.glShader)
  gl.attachShader(glProgram, fragmentShader.glShader)

  gl.bindAttribLocation(glProgram, 0, "a_position")

  val errorBuffer = new StringBuffer()
  var attributeMap: Map[Int, ShaderAttribute] = _
  var attribNames: Map[String, ShaderAttribute] = _
  var uniformMap: Map[String, ShaderUniform] = _

  def attributes: Iterable[String] = attributeMap.values.map(_.name)
  def uniforms: Iterable[String] = uniformMap.keys

  link()

  def release() = {
    factory.released(this)
    gl.detachShader(glProgram, vertexShader.glShader)
    gl.detachShader(glProgram, fragmentShader.glShader)
    gl.deleteProgram(glProgram)
  }

  def link() = {
    gl.linkProgram(glProgram)

    gl.validateProgram(glProgram)
    val ok = gl.getProgramParameter(glProgram, GL.VALIDATE_STATUS).asInstanceOf[Boolean]
    Log("Shader program result: " + ok)
    if (!ok) {
      errorBuffer.append("Shader program validation failed:\n")
      val log = gl.getProgramInfoLog(glProgram)
      Log(log)
      assert(false, errorBuffer)
    }
    // validate that all attributes are bound
    val attributeCount = gl.getProgramParameter(glProgram, GL.ACTIVE_ATTRIBUTES).asInstanceOf[Int]
    Log(s"Attributes in shader program $glProgram: $attributeCount")
    val attribs = mutable.Map[Int, ShaderAttribute]()
    for (i <- 0 until attributeCount) {
      val attrib = gl.getActiveAttrib(glProgram, i)
      val location = gl.getAttribLocation(glProgram, attrib.name)
      //      assert(location == boundIndex)
      val attribute = new ShaderAttribute(location, attrib.name, attrib.size, attrib.`type`)
      attribs.put(location, attribute)
      Log("  Attribute " + attribute)
    }
    attributeMap = attribs.toMap
    attribNames = attributeMap.values.map(a => a.name -> a).toMap
    Log(" Attributes " + attributeMap);
    // find uniformMap
    val uniformCount = gl.getProgramParameter(glProgram, GL.ACTIVE_UNIFORMS).asInstanceOf[Int]
    Log(s"Uniforms in shader program $glProgram: $uniformCount")
    val unis = mutable.Map[String, ShaderUniform]()
    for (i <- 0 until uniformCount) {
      val uni = gl.getActiveUniform(glProgram, i)
      val location = gl.getUniformLocation(glProgram, uni.name)
      val uniform = new ShaderUniform(glProgram, location, uni.size, uni.`type`)
      unis.put(uni.name, uniform)
      Log(s"  Uniform $i, $uniform")
    }
    uniformMap = unis.toMap
  }

  def textureUnit(textureUnit: Int, texture: Texture) = {
    texture.asInstanceOf[JsTexture].bind(textureUnit)
  }

  override def hasUniform(name: String): Boolean = uniformMap.contains(name)

  override def uniform(name: String): Option[Var] = uniformMap.get(name)

  override def hasAttribute(name: String): Boolean = attribNames.contains(name)

  override def attribute(name: String): Option[Var] = attribute(attributeIndex(name))

  override def attribute(index: Int): Option[Var] = {
    gl.disableVertexAttribArray(index) // TODO check
    val attribute = attributeMap(index).asInstanceOf[ShaderAttribute]
    if (attribute == null) {
      return None
    }
    assert(attribute.index == index)
    assert(index > 0, "On some environments attribute index 0 can't be disabled")
//    attribute.debugSize = -1
    Some(attribute)
  }

  def attributeIndex(name: String) = {
    assert(attribNames.contains(name), "No attribute " + name + " in " + attributeMap)
    attribNames(name).index
  }

  def attributeBuffer(name: String, buffer: ShaderBuffer, numComponents: Int) = {
    attributeBuffer(attributeIndex(name), buffer, numComponents)
  }

  def attributeBuffer(index: Int, buffer: ShaderBuffer, numComponents: Int) = {
    val buf = buffer.asInstanceOf[JsShaderBuffer]
    buf.prepareRender()
    gl.enableVertexAttribArray(index)
    gl.vertexAttribPointer(index, numComponents, buf.buffer.glType, buf.normalized, 0, 0)
    //    val attribute = attributeMap(index).debugSize = {
    //      buf.buffer.length / buf.numComponents
    //    }
    //    Log(s"Set attribute $index $buffer")
  }

  override def storageBuffer(bindingIndex: Int, buffer: ShaderBuffer) = {
    val buf = buffer.asInstanceOf[JsShaderBuffer]
    buf.prepareRender()
    // glBindBufferBase(GL_SHADER_STORAGE_BUFFER, 3, buf.glBufferId)
    ???
  }

  def textureBuffer(textureUnit: Int, buffer: ShaderBuffer) = {
    assert(buffer.target == ShaderBuffer.Target.Texture)
    val buf = buffer.asInstanceOf[JsShaderBuffer]
    buf.prepareRender()
    gl.activeTexture(GL.TEXTURE0 + textureUnit)
    ???
//    gl.bindTexture(GL.TEXTURE_BUFFER, buf.glTextureId.get)
//    val format = GL.R32UI //TODO
//    gl.texBuffer(GL.TEXTURE_BUFFER, format, buf.glBufferId)
  }

  import ShaderProgram.Mode
  import ShaderProgram.Mode._

  def glMode(mode: Mode) = mode match {
    case Points        => GL.POINTS
    case LineStrip     => GL.LINE_STRIP
    case LineLoop      => GL.LINE_LOOP
    case Lines         => GL.LINES
    case TriangleStrip => GL.TRIANGLE_STRIP
    case TriangleFan   => GL.TRIANGLE_FAN
    case Triangles     => GL.TRIANGLES
  }

  def draw(mode: ShaderProgram.Mode, index: Int, length: Int) = {
    if (length > 0) {
      assert(index < length)
      for (attrib <- attributeMap.values) {
//        if (attrib.debugSize >= 0) {
        //          assert(attrib.debugSize == length, s"Invalid attribute buffer size in ${attrib.name}: ${attrib.debugSize}, $length")
//        }
//        Log(s"Attribute $attrib")
      }

      gl.useProgram(glProgram)
//        Log(s"drawArrays $index, $length")
      gl.drawArrays(glMode(mode), index, length)
      // assert(gl.getError()==0)
    }
  }

  def draw(indexBuffer: ShaderBuffer, mode: ShaderProgram.Mode, index: Int, length: Int) = {
    val buffer = indexBuffer.asInstanceOf[JsShaderBuffer]
    gl.useProgram(glProgram)
    buffer.prepareRender()
    gl.drawElements(glMode(mode), length, buffer.buffer.glType, index)
  }
}
