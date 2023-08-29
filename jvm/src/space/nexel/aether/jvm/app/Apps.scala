package space.nexel.aether.jvm.app

import space.nexel.aether.jvm.platform.JvmPlatform
import space.nexel.aether.core.platform.PlatformApp
import space.nexel.aether.app.shader.ShaderApp

object Apps {

  val platform = JvmPlatform()

  def launch(app: PlatformApp) = {
    app.run()
  }

  @main
  def shader = launch(ShaderApp(platform))
}
