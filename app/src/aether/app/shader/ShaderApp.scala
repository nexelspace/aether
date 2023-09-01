package aether.app.shader

import aether.core.platform.Platform
import aether.core.platform.Module
import aether.core.platform.Event
import aether.core.platform.Log
import aether.core.graphics.Display
import aether.core.types.Vec2I

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
