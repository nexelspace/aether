package space.nexel.aether.jvm.platform

import space.nexel.aether.core.platform.Platform
import Platform.*
import space.nexel.aether.core.platform.Log
import space.nexel.aether.core.platform.Event
import space.nexel.aether.jvm.graphics.JvmDisplay
import space.nexel.aether.jvm.graphics.InputHandler
import space.nexel.aether.core.platform.EventQueue

class JvmPlatform extends Platform {
  val log = new Log {
    def apply(message: String) = {
      println(message)
    }
  }

  val inputHandler = InputHandler()
  val events = new EventQueue()
  val displayFactory = JvmDisplay.factory(this, inputHandler)

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
