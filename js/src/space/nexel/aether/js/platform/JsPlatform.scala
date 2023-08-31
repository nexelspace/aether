package space.nexel.aether.js.platform

import org.scalajs.dom.raw.{WebGLRenderingContext => GL}
import org.scalajs.dom
import org.scalajs.dom.document
import org.scalajs.dom.HTMLCanvasElement
import space.nexel.aether.core.platform.Platform
import space.nexel.aether.core.platform.Platform.Update
import space.nexel.aether.core.platform.Log
import space.nexel.aether.core.graphics.Display
import space.nexel.aether.core.platform.Event
import space.nexel.aether.js.graphics.*

class JsPlatform extends Platform(Seq(JsDisplay)) {
  val log = new Log {
    def apply(message: String) = {
      println(message)
    }
  }

  val canvas = document.createElement("canvas").asInstanceOf[HTMLCanvasElement]
  val gl = canvas.getContext("webgl2").asInstanceOf[GL]
  val graphics = JsGraphics(gl)

  given Platform = this
  given GL = gl
  val displayFactory = JsDisplay.factory
  val shaderProgramFactory = JsShaderProgram.factory
  val shaderObjectFactory = JsShaderObject.factory
  val shaderBufferFactory = JsShaderBuffer.factory
  val textureFactory = JsTexture.factory


  def run(loop: => Boolean): Unit = {
    def frame(time: Double): Unit = {
      val cont = loop
      if (cont) {
        dom.window.requestAnimationFrame(frame _)
      }
    }
    frame(0)

  }

}
