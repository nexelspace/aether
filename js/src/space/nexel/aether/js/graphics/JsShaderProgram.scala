package space.nexel.aether.js.graphics

import org.scalajs.dom.raw.{WebGLRenderingContext => GL}
import space.nexel.aether.core.platform.Resource
import space.nexel.aether.core.graphics.ShaderProgram.*
import space.nexel.aether.core.graphics.ShaderProgram

object JsShaderProgram {
  def factory(using gl: GL) = new ShaderProgramFactory {
    given ShaderProgramFactory = this
    def apply(config: Config) = new JsShaderProgram(
      config.vertexShader.asInstanceOf[JsShaderObject],
      config.fragmentShader.asInstanceOf[JsShaderObject])
    
  }

}

class JsShaderProgram(vertexShader: JsShaderObject, fragmentShader: JsShaderObject)
    (using factory: ShaderProgramFactory, gl: GL) extends ShaderProgram {
  val program = gl.createProgram()

  gl.attachShader(program, vertexShader.shader)
  gl.attachShader(program, fragmentShader.shader)

  link()

  def link() = {
    gl.linkProgram(program)

    gl.validateProgram(program)
    val ok = gl.getProgramParameter(program, GL.VALIDATE_STATUS).asInstanceOf[Boolean]
    assert(ok, gl.getProgramInfoLog(program))
  }

  def use() = gl.useProgram(program)
  def attribute(name: String): Int = gl.getAttribLocation(program, name)

  def release() = ??? // TODO
}
