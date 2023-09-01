package aether.js.graphics

import aether.core.graphics.Display
import aether.core.platform.*
import aether.core.platform.Module
import aether.core.platform.Module.*
import aether.core.graphics.Display.*
import aether.js.graphics.JsGraphics
import org.scalajs.dom.HTMLCanvasElement
import org.scalajs.dom.raw.{WebGLRenderingContext => GL}
import org.scalajs.dom.document
import aether.core.types.Vec2I
import org.scalajs.dom
import org.scalajs.dom.window

object JsDisplay extends Module {
  def factory(dispatcher: Dispatcher) = new DisplayFactory {
    given DisplayFactory = this
    given Dispatcher = dispatcher
    def createThis(config: Config) = {
      val canvas = document.createElement("canvas").asInstanceOf[HTMLCanvasElement]
      canvas.width = config.size.x
      canvas.height = config.size.y
      document.body.appendChild(canvas)

      JsDisplay(config, canvas)
    }
  }
  def event(event: Event) = event match {
    case Init(platform: Platform) =>
      Log("Init preconfigured displays")
      given Dispatcher = platform.dispatcher

      window.onresize = (e: dom.UIEvent) => {
        platform.displayFactory.instances.foreach {
          case disp: JsDisplay =>
            if (disp.config.fullscreen) disp.resizeToWindow()
        }
      }
      JsEvents.initKeyEvents()
    case u: Update =>
    case Uninit =>
      assert(false, "Uninit unexpected")
      JsEvents.keyMap.reportUndefinedKeys()
    case _ =>
  }
}

class JsDisplay(val config: Config, canvas: HTMLCanvasElement)
    (using factory: DisplayFactory, dispatcher: Dispatcher) extends Display {
  given gl: GL = canvas.getContext("webgl2").asInstanceOf[GL]

  JsEvents.initMouseEvents(canvas)
  JsEvents.initTouchEvents(canvas)

  val graphics = new JsGraphics(gl)
  
  def size: Vec2I = Vec2I(canvas.width, canvas.height)

  def resizeToWindow()(using dispatcher: Dispatcher) = {
    val size = Vec2I(window.innerWidth.toInt, window.innerHeight.toInt)
    // Log(s"resizeToWindow $size")
    canvas.width = size.x
    canvas.height = size.y
    dispatcher.add(Display.Resize(this, size))
  }
  
  def grabPointer(grab: Boolean): Unit = ???

  def render(callback: Display => Unit) = {
    graphics.render(this, callback)
  }

  def release() = ???

}
