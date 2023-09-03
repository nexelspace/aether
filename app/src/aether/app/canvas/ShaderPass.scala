package aether.app.canvas

import ShaderPass._
import aether.core.graphics.ShaderObject
import aether.core.graphics.ShaderProgram
import aether.core.platform.Log
import aether.core.graphics.Graphics

object ShaderPass {
  def create(vertSrc: String, fragSrc: String)(using Graphics) = {
    new ShaderPass(vertSrc, fragSrc)
  }

}

class ShaderPass(var sourceVertex: String, var sourceFragment: String)(using Graphics) {

  var vertex: Option[ShaderObject] = None
  var fragment: Option[ShaderObject] = None
  var program: Option[ShaderProgram] = None

  //create()
  
  def ready = program.isDefined
  
  def create(): Boolean = {
    release()
    try {
      vertex = Some(ShaderObject(ShaderObject.Type.Vertex, sourceVertex))
      fragment = Some(ShaderObject(ShaderObject.Type.Fragment, sourceFragment))
      program = Some(ShaderProgram(vertex.get, fragment.get))
      true
    } catch {
      case e: Exception =>
        Log("Compilation failed:\n"+e.getMessage.split("\n").mkString("  ","\n  ","\n"))
        e.printStackTrace()
        release()
        false
    }
  }
  
  def release() = {
    for (o <- program) o.release()
    for (o <- fragment) o.release()
    for (o <- vertex) o.release()
    program = None
    fragment = None
    vertex = None
  }
  
  def updateFragment(frag: String) = {
    this.sourceFragment = frag
    create()
  }

}


