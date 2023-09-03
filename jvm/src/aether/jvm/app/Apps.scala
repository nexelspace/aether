package aether.jvm.app

import aether.jvm.platform.JvmPlatform
import aether.core.platform.Module
import aether.app.shader.ShaderApp
import aether.app.mandelbrot.Mandelbrot
import aether.app.canvas.CanvasApp

object Apps {

  val platform = JvmPlatform()

  def launch(app: Module) = {
    platform.runApp(app)
  }

  @main
  def shader = launch(ShaderApp(platform))
  @main
  def mandelbrot = launch(Mandelbrot(platform))
  @main
  def canvas = launch(CanvasApp(platform))
}
