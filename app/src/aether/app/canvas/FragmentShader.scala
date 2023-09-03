package aether.app.canvas

import aether.core.types.RectI
import aether.core.types.Vec2I
import aether.core.types.VecExt.*
import aether.lib.graphics.Mesh
import aether.lib.graphics.ShaderVarBuffer

import scala.collection.mutable.LinkedHashMap

import FragmentShader.RenderParams
import aether.core.graphics.ShaderProgram
import aether.core.types.Mat3F
import aether.core.graphics.Graphics
import aether.lib.graphics.Mesh

object FragmentShader {
  
  private var mesh_ : Mesh = null
  
  def mesh(using Graphics) = {
    if (mesh_ == null) mesh_ = createMesh
    mesh_
  }

  def createMesh(using Graphics): Mesh = {
    val sizeX = 1 //Global.defaultSize
    val sizeY = 1 //Global.defaultSize
    val mesh = Mesh.factory.staticPT(4)
    mesh.positions.put2F(-1, 1)
    mesh.positions.put2F(1, 1)
    mesh.positions.put2F(-1, -1)
    mesh.positions.put2F(1, -1)
    mesh.texCoords.put2F(0, sizeY)
    mesh.texCoords.put2F(sizeX, sizeY)
    mesh.texCoords.put2F(0, 0)
    mesh.texCoords.put2F(sizeX, 0)
    mesh
  }

  case class RenderParams(resolution: Vec2I, viewport: RectI, tx: Mat3F)

}

class FragmentShader()(using Graphics) {

  var pass: ShaderPass = new ShaderPass(null, null)

  var frame = 0
  val startTime = System.currentTimeMillis()

  def ready = pass.ready

  var version = "300 es"

  //TODO Replace define declarations in sources
  def header: String = {
    val lines = s"#version $version" +: defines.map {
      case (name, value) => s"#define $name $value"
    }.toSeq
    lines.mkString("\n")
  }

  val defines = LinkedHashMap[String, String]()

  def setSource(vertShader: String, fragShader: String) = {
    //TODO Replace define declarations in sources
    pass.sourceVertex = /*header + */vertShader
    pass.sourceFragment = /*header + */fragShader
    pass.create()
  }

  def render(params: RenderParams) = {
    assert(pass.ready, "Incomplete ShaderPass")
    frame = frame + 1

    val program = pass.program.get
    FragmentShader.mesh.toProgram(program)

    program.uniform("iResolution").foreach(_.put2F(params.resolution.toVec2F))
    program.uniform("iFrame").foreach(_.putI(frame))
    program.uniform("iTime").foreach(_.putF((System.currentTimeMillis - startTime) * 0.001f))
    //    program.uniform("iMouse").foreach(_.put2F(mousePos))

    program.uniform("iViewport").foreach(_.put4F(params.viewport.bounds.toVec4F))
    program.uniform("iTransform").foreach(_.putMat3F(params.tx))

    program.uniform("iIterations").foreach(_.putI(100))

    //program.uniform("iMVP").foreach(_.putMat4F(mvp))
    //program.uniform("iCamera").foreach(_.putMat4F(camera))
    //    for (i <- 0 until channels.size) {
    //      program.textureUnit(i, channels(i))
    //      program.uniform(s"iChannel$i").get.putI(i)
    //    }

    program.draw(ShaderProgram.Mode.TriangleStrip, 0, 4)

  }

  def getState(): Seq[String] = {
    val program = pass.program.get
    val unis = for (name <- program.uniforms) yield program.uniform(name).get.toString()
    val att = for (name <- program.attributes) yield program.attribute(name).get.toString()
    (unis ++ att).toSeq
  }
}
