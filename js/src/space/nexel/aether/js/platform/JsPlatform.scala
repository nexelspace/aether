package space.nexel.aether.js.platform

import space.nexel.aether.core.platform.Platform
import space.nexel.aether.core.platform.Platform.Update
import space.nexel.aether.core.platform.Log
import space.nexel.aether.core.platform.Display
import space.nexel.aether.core.platform.Event
import space.nexel.aether.core.platform.EventQueue
import org.scalajs.dom

class JsPlatform extends Platform {
  val log = new Log {
    def apply(message: String) = {
      println(message)
    }
  }

  val displayFactory = JsDisplay.factory

  val events = new EventQueue()

  def run(handler: Event => Boolean): Unit = {
    def loop(time: Double): Unit = {
      val time = System.currentTimeMillis()
      val cont = handler(Update(time))

      // if (cont) {
      //   val delay = Math.abs(time + interval - System.currentTimeMillis())
      //   dom.window.setTimeout(loop _, delay)
      // }
      if (cont) {
        dom.window.requestAnimationFrame(loop _)
      }
    }
    loop(0)

  }

}
