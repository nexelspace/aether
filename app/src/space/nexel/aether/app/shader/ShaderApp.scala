package space.nexel.aether.app.shader

import space.nexel.aether.core.platform.Platform
import space.nexel.aether.core.platform.Module
import space.nexel.aether.core.platform.Event
import space.nexel.aether.core.platform.Log

class ShaderApp(val platform: Platform) extends Module {

  def event(event: Event) = {
    event match {
      case event => Log(s"event $event")
    }
  }
}
