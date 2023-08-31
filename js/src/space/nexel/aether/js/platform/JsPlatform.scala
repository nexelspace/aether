package space.nexel.aether.js.platform

import space.nexel.aether.core.platform.Platform.Update
import space.nexel.aether.core.platform.*
import org.scalajs.dom
import space.nexel.aether.js.graphics.JsDisplay
import space.nexel.aether.core.base.HttpBase

class JsPlatform extends Platform(Seq(JsDisplay)) {
  val log = new Log {
    def apply(message: String) = {
      println(message)
    }
  }
  val base = new HttpBase(new JsHttpClient(), dom.window.location.origin)

  val displayFactory = JsDisplay.factory(dispatcher)

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
