package space.nexel.aether.app.shader

import space.nexel.aether.core.platform.Platform
import space.nexel.aether.core.platform.PlatformApp
import space.nexel.aether.core.platform.Log

class ShaderApp(val platform: Platform) extends PlatformApp {
  def run() = {
    Log("Starting shader app")
  }
}
