package aether.js.platform

import aether.core.platform.Platform.Update
import aether.core.platform.*
import org.scalajs.dom
import aether.js.graphics.JsDisplay
import aether.core.base.HttpBase

class JsPlatform extends Platform(Seq(JsDisplay)) {
  val name = Platform.Name.Js
  val log = new Log {
    def apply(message: String) = {
      println(message)
    }
  }
  val http = new JsHttpClient()
  val origin = dom.window.location.origin
  val base = new HttpBase(http, origin)
  val resourceBase  = new HttpBase(new JsHttpClient(), s"$origin/resources")

  val resourcePath: String = "resource"

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
