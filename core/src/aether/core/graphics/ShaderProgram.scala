package aether.core.graphics

import aether.core.platform.Resource
import aether.core.platform.NativeResource
import aether.core.graphics.ShaderObject.ShaderObjectFactory
import aether.core.buffers.Var
import ShaderProgram.*

object ShaderProgram {

  type ShaderProgramFactory = Resource.Factory[ShaderProgram, Config]

  enum Mode {
    case Points, LineStrip, LineLoop, Lines, TriangleStrip, TriangleFan, Triangles
  }

  case class Config(
      vertexShader: ShaderObject,
      fragmentShader: ShaderObject
  ) extends Resource.Config

  def apply(vertexShader: ShaderObject, fragmentShader: ShaderObject)(using graphics: Graphics): ShaderProgram =
    graphics.shaderProgramFactory.create(Config(vertexShader, fragmentShader))

  def apply(vertexSource: String, fragmentSource: String)(using graphics: Graphics): ShaderProgram = {
    val vertex = ShaderObject(ShaderObject.Type.Vertex, vertexSource)
    val fragment = ShaderObject(ShaderObject.Type.Fragment, fragmentSource)
    apply(vertex, fragment)
  }
}

trait ShaderProgram extends NativeResource[ShaderProgram, ShaderProgram.Config] {
  def error: Option[String]

  def bindAttribute(index: Int, attributeName: String): Unit

  def textureUnit(textureUnit: Int, texture: Texture): Unit

  def uniforms: Iterable[String]

  def hasUniform(name: String): Boolean

  def uniform(name: String): Option[Var]

  def attributes: Iterable[String]

  def hasAttribute(name: String): Boolean

  def attribute(name: String): Option[Var]

  def attribute(index: Int): Option[Var]

  def attributeBuffer(name: String, buffer: ShaderBuffer, numComponents: Int): Unit

  def attributeBuffer(index: Int, buffer: ShaderBuffer, numComponents: Int): Unit

  def storageBuffer(bindingIndex: Int, buffer: ShaderBuffer): Unit

  def textureBuffer(bindingIndex: Int, buffer: ShaderBuffer): Unit

  def draw(mode: Mode, index: Int, length: Int): Unit

  def draw(indexBuffer: ShaderBuffer, mode: Mode, index: Int, length: Int): Unit

  override def toString: String = {
    val attribs = attributes.mkString(", ")
    val unis = uniforms.mkString(", ")
    s"[A:{$attribs}, U:{$unis}]"
  }
}
