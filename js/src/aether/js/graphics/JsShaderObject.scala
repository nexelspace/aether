package aether.js.graphics

import org.scalajs.dom.raw.{WebGLRenderingContext => GL}
import aether.core.graphics.ShaderObject
import aether.core.graphics.ShaderObject.Type
import aether.core.graphics.ShaderObject.Type.*

import aether.core.platform.*
import aether.core.graphics.ShaderObject.*

object JsShaderObject {
  def factory(using gl: GL) = new ShaderObjectFactory {
    given ShaderObjectFactory = this
    def createThis(config: Config) = new JsShaderObject(config.typ, config.source.get)
  }
}

class JsShaderObject(typ: Type, source: String)(using factory: ShaderObjectFactory, gl: GL) extends ShaderObject {

  val glShaderType = typ match {
    case Type.Fragment => GL.FRAGMENT_SHADER
    case Type.Vertex   => GL.VERTEX_SHADER
  }

  val glShader = gl.createShader(glShaderType)
  gl.shaderSource(glShader, source)
  compile()

  //  def this(id: Int, flags: Int, file: Ref) {
  //    this(id, flags, "not implemented")
  //  }

  def compile(): Boolean = {
    gl.compileShader(glShader)
    val compiled = gl.getShaderParameter(glShader, GL.COMPILE_STATUS).asInstanceOf[Boolean]
    if (compiled) true
    else {
      val log = gl.getShaderInfoLog(glShader)
      Log(s"Failed to compile shader")
      Log(log)
      false
    }
  }

  def release() = {
    factory.released(this)
    gl.deleteShader(glShader)
  }


}
