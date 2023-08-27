package space.nexel.aether.js.graphics

import org.scalajs.dom.raw.{WebGLRenderingContext => GL}
import space.nexel.aether.core.graphics.ShaderObject
import space.nexel.aether.core.graphics.ShaderObject.Type
import space.nexel.aether.core.graphics.ShaderObject.Type.*

import space.nexel.aether.core.platform.Resource
import space.nexel.aether.core.graphics.ShaderObject.*

object JsShaderObject {
  def factory(using gl: GL) = new ShaderObjectFactory {
    given ShaderObjectFactory = this
    def apply(config: Config) = new JsShaderObject(config.typ)
  }
}

class JsShaderObject(typ: Type)(using factory: ShaderObjectFactory, gl: GL) extends ShaderObject {
  def glShader = typ match {
    case Fragment => GL.FRAGMENT_SHADER
    case Vertex => GL.VERTEX_SHADER
  }
  val shader = gl.createShader(glShader)

  def load(source: String) = {
    gl.shaderSource(shader, source)
    gl.compileShader(shader);
    val ok = gl.getShaderParameter(shader, GL.COMPILE_STATUS).asInstanceOf[Boolean]
    assert(ok, gl.getShaderInfoLog(shader))
  }

  def release() = {
    gl.deleteShader(shader)

  }

}
