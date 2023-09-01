package aether.jvm.graphics

import aether.core.graphics.*
import aether.core.graphics.Graphics.*

import aether.core.types.Vec2I
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW._
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWVidMode
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL12._
import org.lwjgl.opengl.GL13._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._
import org.lwjgl.opengl.GL41._
import aether.core.types.RectI
import JvmGraphics.*

object JvmGraphics {
  lazy val frameBuffer = glGenFramebuffers()

}

class JvmGraphics extends Graphics {
  given Graphics = this
  val shaderProgramFactory = JvmShaderProgram.factory
  val shaderObjectFactory = JvmShaderObject.factory
  val shaderBufferFactory = JvmShaderBuffer.factory
  val textureFactory = JvmTexture.factory

  val dispScale = 1f
  var target: RenderTarget = _

  def render(disp: Display, callback: (Display) => Unit) = {
    target = disp
    initRendering(RectI(0, 0, target.size.x, target.size.y), true)
    glBindFramebuffer(GL_FRAMEBUFFER, 0)

    callback(disp)

    target = null

  }

  private def initRendering(area: RectI, targetDisplay: Boolean) = {
    // privateSize = area.size

    // set immutable state
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_BORDER)
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_BORDER)

    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
    glTexParameteri(GL_TEXTURE_CUBE_MAP, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE)

    // reset mutable state
    // resetState()
  }

  def setTargetDisplay(display: Display) = {
    target match {
      case d: JvmDisplay if (d == display) =>
      case _ =>
        target = display
        initRendering(RectI(0, 0, target.size.x, target.size.y), true)
        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }
  }

  def setTargetTexture(texture: Texture, area: RectI) = {
    target match {
      case t: JvmTexture if (t == texture) =>
      case _ =>
        assert(texture.isRenderTarget, s"Texture targetTexture is not renderable");
        val tex = texture.asInstanceOf[JvmTexture]
        target = texture

        val targetArea = if (area != null) area else RectI(0, 0, target.size.x, target.size.y)
        initRendering(targetArea, false)
        tex.prepareRenderTarget()

        assert(frameBuffer > 0)
        glBindFramebuffer(GL_FRAMEBUFFER, frameBuffer)
        val glTextureId = tex.glTextureId
        tex.bind(0)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, glTextureId, 0)
        // attach texture depth buffer if available
        //		if (depthTextureId>0) {
        //			val depthTexture = ETexture.get(depthTextureId)
        //			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture.glTextureId, 0)
        //		} else {
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, 0, 0)
      //		}
    }
  }

  def clear(r: Float, g: Float, b: Float, a: Float): Unit = {
    import org.lwjgl.opengl.GL11._
    glClearColor(r, g, b, a)
    glClear(GL_COLOR_BUFFER_BIT)
  }

  def viewport_=(viewport: RectI) = {
    val actual = RectI(0, 0, size.x, size.y)
    state.viewport = if (viewport == null) actual else viewport
    val v = (state.viewport.toRectF * dispScale).toRectI
    // glViewport(v.x, activeDisplay.sizeY - v.y - v.sizeY, v.sizeX, v.sizeY)
    glViewport(v.x, v.y, v.sizeX, v.sizeY)
  }

  def clip_=(clip: RectI) = {
    val view = RectI(0, 0, viewport.sizeX, viewport.sizeY)
    state.clip = if (clip == null) view else clip
    if (clip == null) {
      glDisable(GL_SCISSOR_TEST)
      // glScissor(0, 0, activeDisplay.sizeX, activeDisplay.sizeY)
    } else {
      glEnable(GL_SCISSOR_TEST)
      val c = state.clip
      glScissor(c.x, c.y, c.sizeX, c.sizeY)
    }
  }

  def blend_=(blend: Blend) = {
    state.blend = blend
    glEnable(GL_BLEND)
    blend match {
      case Blend.Normal ⇒ glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
      case Blend.NormalPremultiplied ⇒ glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA)
      case Blend.Additive ⇒ glBlendFunc(GL_SRC_ALPHA, GL_ONE)
      case Blend.None ⇒ glDisable(GL_BLEND)
    }
  }

  def cull_=(cull: Cull) = {
    state.cull = cull
    cull match {
      case Cull.None ⇒ glDisable(GL_CULL_FACE)
      case Cull.CW ⇒ glEnable(GL_CULL_FACE)
    }
  }

  def depthTest_=(depthTest: DepthTest): Unit = {
    state.depthTest = depthTest
    if (depthTest == DepthTest.Always) {
      glDisable(GL_DEPTH_TEST)
    } else {
      glEnable(GL_DEPTH_TEST)
      glDepthFunc(GL_NEVER + (depthTest.flags & 0x7))
      glDepthMask((depthTest.flags & DepthTest.Write.flags) != 0)
      if ((depthTest.flags & DepthTest.Range1to0.flags) != 0) {
        glDepthRangef(1, 0)
      } else {
        glDepthRangef(0, 1)
      }
    }
  }

  def filter_=(filter: Filter) = {
    state.filter = filter
    // filter GL state is initialized when texture is bound
  }
}
