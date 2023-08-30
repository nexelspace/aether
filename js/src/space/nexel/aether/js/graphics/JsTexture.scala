package space.nexel.aether.js.graphics

import org.scalajs.dom.raw.{WebGLRenderingContext => GL}
import org.scalajs.dom.Image
import space.nexel.aether.core.platform.Resource
import space.nexel.aether.core.platform.Log
import space.nexel.aether.core.platform.Dispatcher
import space.nexel.aether.core.graphics.Texture
import space.nexel.aether.core.graphics.Texture.*
import space.nexel.aether.core.types.Vec2I

object JsTexture {
  def factory(using gl: GL) = new TextureFactory {
    given TextureFactory = this
    def apply(config: Config) = new JsTexture(config.size.get)

    override def load(url: String, config: Config)(using dispatcher: Dispatcher) = {
      val resource = Resource[Texture]()
      val image = new Image()
      image.src = url
      // image.onload = { event =>
      image.addEventListener("load", _ => load())
      image.addEventListener("error", _ => error())
      def load() = {
        Log(s"Image $url loaded")
        resource.set(new JsTexture(image))
      }
      def error() = {
        resource.error = s"Failed to load image $url"
      }
      resource
    }
  }
}

class JsTexture(val size: Vec2I)(using factory: TextureFactory, gl: GL) extends Texture {
  val texture = gl.createTexture()

  def bind() = gl.bindTexture(GL.TEXTURE_2D, texture)

  def this(image: Image)(using factory: TextureFactory, gl: GL) = {
    this(Vec2I(image.width, image.height))
    bind()
    gl.texImage2D(GL.TEXTURE_2D, 0, GL.RGBA, GL.RGBA, GL.UNSIGNED_BYTE, image)
  }

  def release() = gl.deleteTexture(texture)
}
