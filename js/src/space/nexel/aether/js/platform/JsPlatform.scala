package space.nexel.aether.js.platform

import space.nexel.aether.core.platform.Platform
import space.nexel.aether.core.platform.Platform.Update
import space.nexel.aether.core.platform.Log
import space.nexel.aether.core.graphics.Display
import space.nexel.aether.core.platform.Event
import org.scalajs.dom
import space.nexel.aether.js.graphics.JsDisplay

class JsPlatform extends Platform(Seq(JsDisplay)) {
  val log = new Log {
    def apply(message: String) = {
      println(message)
    }
  }

  val displayFactory = JsDisplay.factory

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
