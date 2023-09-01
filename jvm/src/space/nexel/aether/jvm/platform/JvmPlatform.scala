package space.nexel.aether.jvm.platform

import space.nexel.aether.core.platform.Platform
import Platform.*
import space.nexel.aether.core.platform.Log
import space.nexel.aether.core.platform.Event
import space.nexel.aether.jvm.graphics.JvmDisplay
import space.nexel.aether.core.platform.Dispatcher
import space.nexel.aether.core.graphics.Display
import java.nio.file.Paths

class JvmPlatform() extends Platform(Seq(JvmDisplay)) {
  val log = new Log {
    def apply(message: String) = {
      println(message)
    }
  }

  val wd = Paths.get("").toAbsolutePath().toString().replaceAll("\\\\", "/")
  val base = new FileBase(wd)

  val resourceBase  = new FileBase("app/src")

  val displayFactory = JvmDisplay.factory(this)

  def run(loop: => Boolean): Unit = {
    while (loop) Thread.`yield`()
  }
}
