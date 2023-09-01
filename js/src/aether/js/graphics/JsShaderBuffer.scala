package aether.js.graphics

import aether.core.buffers.BufferWrapper
import aether.core.graphics.ShaderBuffer
import aether.core.graphics.ShaderBuffer.*
import aether.core.types.Num
import aether.js.buffers.JsBuffer
import org.scalajs.dom.webgl.{RenderingContext => GL}

import scala.scalajs.js.typedarray.Float32Array
import scala.scalajs.js.typedarray.Int16Array
import scala.scalajs.js.typedarray.Int32Array
import scala.scalajs.js.typedarray.Int8Array
import scala.scalajs.js.typedarray.TypedArray
import scala.scalajs.js.typedarray.Uint16Array
import scala.scalajs.js.typedarray.Uint32Array
import scala.scalajs.js.typedarray.Uint8Array

object JsShaderBuffer {
  def factory(using gl: GL) = new ShaderBufferFactory {
    given ShaderBufferFactory = this
    def createThis(config: Config) = new JsShaderBuffer(config)
  }

}

class JsShaderBuffer(config: ShaderBuffer.Config)(using factory: ShaderBufferFactory, gl: GL) extends ShaderBuffer(config) with BufferWrapper {

  override val dataType: Num = ShaderBuffer.dataType(config.flags)
  override val target = config.flags & Target.Mask

  val buffer: JsBuffer[_] = JsBuffer.create(config.dataType, config.capacity)
  val normalized = (config.flags & Flag.Normalize) != 0

  override def putI(v: Int): Unit = buffer.putI(v)
  override def putF(v: Float): Unit = buffer.putF(v)

  override def getI(): Int = buffer.getI()
  override def getF(): Float = buffer.getF()
  //  override def getD(): Double = buffer.getD()

  override def resizeBuffer(size: Int): Unit = {
    assert(config.dynamic)
    buffer.resizeBuffer(size)
  }

  val glTarget = target match {
    case Target.Vertex => GL.ARRAY_BUFFER
    case Target.Index  => GL.ELEMENT_ARRAY_BUFFER
  }

  def arrayBuffer: scala.scalajs.js.typedarray.ArrayBuffer = buffer.array.buffer

  val glBuffer = gl.createBuffer()

  def release() = {
    factory.released(this)
    gl.deleteBuffer(glBuffer)
  }

  def prepareRender() = {
    gl.bindBuffer(glTarget, glBuffer)
    gl.bufferData(glTarget, arrayBuffer, GL.DYNAMIC_DRAW)
  }

  override def toString: String = {
    buffer.toString
  }

}
