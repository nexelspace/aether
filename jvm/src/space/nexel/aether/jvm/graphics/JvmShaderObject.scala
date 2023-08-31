package space.nexel.aether.jvm.graphics

import space.nexel.aether.core.graphics.ShaderObject
import space.nexel.aether.core.platform.Resource
import space.nexel.aether.core.graphics.ShaderObject.*
import space.nexel.aether.core.graphics.ShaderObject.Type.*
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL13._
import org.lwjgl.opengl.GL15._
import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL30._
import org.lwjgl.opengl.GL31._
import org.lwjgl.opengl.GL41._


object JvmShaderObject {
  val factory = new Resource.Factory[ShaderObject, ShaderObject.Config] {
    given ShaderObjectFactory = this
    def createThis(config: Config) = new JvmShaderObject(config)
  }
}

class JvmShaderObject(config: Config)(using factory: ShaderObjectFactory) extends ShaderObject {
  def release() = {
    factory.released(this)
    glDeleteShader(glShader)
  }

  val glShaderType = config.typ match {
    case Fragment => GL_FRAGMENT_SHADER
    case Vertex => GL_VERTEX_SHADER
  }

  val glShader = glCreateShader(glShaderType)

  assert(config.source != null)
  glShaderSource(glShader, config.source.get)

  compile()

  def compile(): Boolean = {
    glCompileShader(glShader)
    val compiled = new Array[Int](1)
    glGetShaderiv(glShader, GL_COMPILE_STATUS, compiled)
    val ok = compiled(0)!=0
    if (!ok) throw new RuntimeException(s"Failed to compile ${config.typ} shader:\n" + getLog())
    true
  }

  def getLog(): String = {
    val logLength = new Array[Int](1)
    glGetShaderiv(glShader, GL_INFO_LOG_LENGTH, logLength)

    val log = new Array[Byte](logLength(0))
    if (log.length > 0) {
      glGetShaderInfoLog(glShader, logLength(0))
    } else "[no log]"

  }

}
