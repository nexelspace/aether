package space.nexel.aether.js.sys

import space.nexel.aether.core.platform.Display
import space.nexel.aether.core.platform.Resource
import space.nexel.aether.core.platform.Display.*
import space.nexel.aether.js.graphics.JsGraphics
import org.scalajs.dom.HTMLCanvasElement
import org.scalajs.dom.raw.{WebGLRenderingContext => GL}
import org.scalajs.dom.document
import space.nexel.aether.core.types.Vec2I

object JsDisplay {
  val factory = new DisplayFactory {
    given DisplayFactory = this
    def apply(config: Config) = {
      val canvas = document.createElement("canvas").asInstanceOf[HTMLCanvasElement]
      canvas.width = 1024
      canvas.height = 512
      document.body.appendChild(canvas)

      JsDisplay(canvas)
    }
  }
}

class JsDisplay(canvas: HTMLCanvasElement)(using factory: DisplayFactory) extends Display {
  given gl: GL = canvas.getContext("webgl2").asInstanceOf[GL]

  def graphics = new JsGraphics()
  def size: Vec2I = Vec2I(canvas.width, canvas.height)

  def grabPointer(grab: Boolean): Unit = ???


  def render(callback: => Unit) = {
    // TODO
    callback
  }

  def release() = ???

}
