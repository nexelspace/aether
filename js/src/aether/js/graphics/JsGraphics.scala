package aether.js.graphics

import org.scalajs.dom.raw.{WebGLRenderingContext => GL}
import aether.core.types.*
import aether.core.graphics.*
import aether.core.graphics.Graphics.*
import aether.core.platform.Log

class JsGraphics(val gl: GL) extends Graphics {
  given GL = gl
  val shaderProgramFactory = JsShaderProgram.factory
  val shaderObjectFactory = JsShaderObject.factory
  val shaderBufferFactory = JsShaderBuffer.factory
  val textureFactory = JsTexture.factory(this)

  var target: RenderTarget = _

  // ---- public interface

  def render(disp: Display, callback: (Display) => Unit) = {
    target = disp
    callback(disp)
    target = null
  }

  def setTargetDisplay(display: Display) = {
    ??? // TODO
    target match {
      case d: JsDisplay if (d == display) =>
      case _ =>
        target = display
        gl.bindFramebuffer(GL.FRAMEBUFFER, null)
    }
  }

  def setTargetTexture(texture: Texture, area: RectI) = {
    ??? // TODO
    target match {
      case t: JsTexture if (t == texture) =>
      case _ =>
        ???
    }
  }

  def clear(r: Float, g: Float, b: Float, a: Float): Unit = {
    gl.clearColor(r, g, b, a)
    gl.clear(GL.COLOR_BUFFER_BIT | GL.DEPTH_BUFFER_BIT)
  }

  def viewport_=(view: RectI) = {
    val actual = RectI(0, 0, gl.drawingBufferWidth, gl.drawingBufferHeight)
    state.viewport = if (view == null) actual else view
    val v = state.viewport
    gl.viewport(v.x, v.y, v.sizeX, v.sizeY)
  }

  def clip_=(clip: RectI) = {
    val view = RectI(0, 0, viewport.sizeX, viewport.sizeY)
    state.clip = if (clip == null) view else clip
    if (clip == null) {
      gl.disable(GL.SCISSOR_TEST)
    } else {
      gl.enable(GL.SCISSOR_TEST)
      val c = state.clip
      gl.scissor(c.x, c.y, c.sizeX, c.sizeY)
    }
  }

  def blend_=(blend: Blend) = {
    state.blend = blend
    gl.enable(GL.BLEND)
    blend match {
      case Blend.Normal              => gl.blendFunc(GL.SRC_ALPHA, GL.ONE_MINUS_SRC_ALPHA)
      case Blend.NormalPremultiplied => gl.blendFunc(GL.ONE, GL.ONE_MINUS_SRC_ALPHA)
      case _                         => ???
    }
  }

  def cull_=(cull: Cull) = {
    state.cull = cull
    cull match {
      case Cull.None => gl.disable(GL.CULL_FACE)
      case Cull.CW   => gl.enable(GL.CULL_FACE)
    }
  }

  def depthTest_=(depthTest: DepthTest): Unit = {
    state.depthTest = depthTest
    if (depthTest == DepthTest.Always) {
      gl.disable(GL.DEPTH_TEST)
    } else {
      gl.enable(GL.DEPTH_TEST)
      gl.depthFunc(GL.NEVER + (depthTest.flags & 0x7))
      gl.depthMask((depthTest.flags & DepthTest.Write.flags) != 0)
      if ((depthTest.flags & DepthTest.Range1to0.flags) != 0) {
        gl.depthRange(1, 0)
      } else {
        gl.depthRange(0, 1)
      }
    }
  }

  def filter_=(filter: Filter) = {
    state.filter = filter
    // filter GL state is initialized when texture is bound
  }
}
