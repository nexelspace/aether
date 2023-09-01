package aether.js.app

import scala.scalajs.js.annotation.JSExport
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.scalajs.js.annotation.JSExportAll
import aether.core.platform.Module
import aether.js.platform.JsPlatform
import aether.app.shader.ShaderApp
import aether.app.mandelbrot.Mandelbrot

@JSExportTopLevel("App")
@JSExportAll
object apps {

  val platform = JsPlatform()

  def launch(appName: String, app: Module) = {
    // val base = HttpBase(sys.httpClient, s"/app/$appName/res")
    platform.runApp(app)
  }

  @main
  def shader() = launch("shader", new ShaderApp(platform))

  @main
  def mandelbrot() = launch("mandelbrot", new Mandelbrot(platform))


}