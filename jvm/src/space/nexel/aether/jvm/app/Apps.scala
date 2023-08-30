package space.nexel.aether.jvm.app

import space.nexel.aether.jvm.platform.JvmPlatform
import space.nexel.aether.core.platform.Module
import space.nexel.aether.app.shader.ShaderApp

object Apps {

  val platform = JvmPlatform()

  def launch(app: Module) = {
    platform.runApp(app)
  }

  @main
  def shader = launch(ShaderApp(platform))
}
