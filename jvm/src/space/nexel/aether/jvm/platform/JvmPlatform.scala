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
  

  // def run(handler: (Event) => Boolean): Unit = {
  //   var cont = true
  //   while (!requestExit && cont) {
  //     val time = System.currentTimeMillis()
      
  //     cont = handler(Update(time))
  //     // Thread.sleep(10)
  //     Thread.`yield`()
  //   }
  // }
}
