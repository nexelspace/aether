package space.nexel.aether.jvm.app

import space.nexel.aether.jvm.platform.JvmPlatform
import space.nexel.aether.core.platform.PlatformApp
import space.nexel.aether.app.shader.ShaderApp

object Apps {

  val platform = JvmPlatform()

  def launch(app: PlatformApp) = {
    // platform.launch(app)
  }

  @main
  def canvas = launch(ShaderApp(platform))
}
