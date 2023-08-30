package space.nexel.aether.core.platform

import space.nexel.aether.core.graphics.Display

class RenderLoop(platform: Platform, modules: Seq[Module]) {

  private var running = true

  def stop = running = false

  def run() = {
    Log("Module init")
    modules.foreach(_.event(Module.Init()))
    Log("Start loop")
    while (running) {
      platform.displayFactory.instances.foreach(_.render { disp =>
        modules.foreach(_.event(Display.Paint(disp)))
      })
    }
    Log("Module uninit")
    modules.reverse.foreach(_.event(Module.Uninit))
    Log("Exit")
  }
}
