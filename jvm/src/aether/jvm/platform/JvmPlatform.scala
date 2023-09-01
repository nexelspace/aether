package aether.jvm.platform

import aether.core.platform.Platform
import Platform.*
import aether.core.platform.Log
import aether.core.platform.Event
import aether.jvm.graphics.JvmDisplay
import aether.core.platform.Dispatcher
import aether.core.graphics.Display
import java.nio.file.Paths

class JvmPlatform() extends Platform(Seq(JvmDisplay)) {
  val name = Platform.Name.Jvm
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
