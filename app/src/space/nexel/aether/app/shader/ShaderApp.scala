package space.nexel.aether.app.shader

import space.nexel.aether.core.platform.Platform
import space.nexel.aether.core.platform.Module
import space.nexel.aether.core.platform.Event
import space.nexel.aether.core.platform.Log
import space.nexel.aether.core.graphics.Display
import space.nexel.aether.core.types.Vec2I

class ShaderApp(val platform: Platform) extends Module {

  val display = platform.displayFactory.create(Display.Config(size = Vec2I(512, 512)))

  def event(event: Event) = {
    event match {
      case Display.Paint(display) => {
        Log(s"Paint")
        display.graphics.clear(1,0,0,1)
      }
      case event => Log(s"event $event")
    }
  }
}
