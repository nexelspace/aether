package space.nexel.aether.jvm.platform

import space.nexel.aether.core.platform.Platform
import Platform.*
import space.nexel.aether.core.platform.Log
import space.nexel.aether.core.platform.Event
import space.nexel.aether.jvm.graphics.JvmDisplay
import space.nexel.aether.core.platform.Dispatcher
import space.nexel.aether.core.graphics.Display

class JvmPlatform() extends Platform(Seq(JvmDisplay)) {
  val log = new Log {
    def apply(message: String) = {
      println(message)
    }
  }

  val displayFactory = JvmDisplay.factory(this)

  def run(loop: => Boolean): Unit = {
    var cont = true
    while (cont) {
      cont = loop
       Thread.`yield`()
    }
  }
}

