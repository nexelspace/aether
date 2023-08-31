package space.nexel.aether.core.platform

import space.nexel.aether.core.graphics.Display

class RenderLoop(platform: Platform, modules: Seq[Module]) {

  private var running = true

  def stop() = running = false

  def run() = {
    while (running) {

      var processEvents = true
      while (processEvents) {
        platform.dispatcher.getEvent() match {
          case Some(event) => modules.foreach(_.event(event))
          case None        => processEvents = false
        }
      }

      platform.displayFactory.instances.foreach(_.render { disp =>
        modules.foreach(_.event(Display.Paint(disp)))
      })
    }
    modules.reverse.foreach(_.event(Module.Uninit))
  }
}
