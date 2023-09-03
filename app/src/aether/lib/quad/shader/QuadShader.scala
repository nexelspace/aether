package aether.lib.quad.shader

import aether.lib.quad.Quad
import aether.core.types.Vec4
import aether.core.types.Vec2F
import aether.core.util.Colors
import aether.core.graphics.ShaderProgram
import aether.core.graphics.ShaderBuffer.*
import aether.lib.types.Transform2F
import aether.lib.graphics.ShaderVarBuffer
import aether.core.graphics.Graphics
import aether.lib.util.MatUtil

object QuadShader {

  def colorize(root: Quad[Int]): Quad[Int] with Quad.Id[Int] = {
    def avg(c: IndexedSeq[Int]): Int = Colors.mix(Colors.mix(c(0), c(1)), Colors.mix(c(2), c(3)))
    Quad.decorate(root, avg)
  }

  def linearize(root: Quad[Int]): Seq[Int] = {
    val buffer = new SymSeq()
    def color(rgba: Int) = rgba//if ((rgba & 0xff) == 0) rgba | 0xff else rgba
    val headerBranch = 0x100
    def populate(node: Quad[Int]): Unit = {
      node match {
        case branch: Quad.Branch[Int] ⇒
          buffer.add(headerBranch)
          val ptrs = (0 until 3).map(_ ⇒ buffer.pointer())
          populate(branch.quads.head)
          for ((quad, ptr) ← branch.quads.tail zip ptrs) {
            buffer.target(ptr)
            populate(quad)
          }
        case leaf: Quad.Leaf[Int] ⇒
          buffer.add(color(leaf.id))
      }
    }
    populate(colorize(root))
    buffer.linearize()
  }
}

class QuadShader(val program: ShaderProgram)(using g: Graphics) {

  var released = false

  val vertexBuffer = ShaderVarBuffer(Target.Vertex | Type.Float, 6, 2)

  def put(x: Float, y: Float) = vertexBuffer.put2F(Vec2F(x, y))
  put(-1, -1)
  put(-1, +1)
  put(+1, -1)
  put(+1, -1)
  put(-1, +1)
  put(+1, +1)

  val iterations = 32
  var pointerStart = Vec2F.Zero
  var mousePos = Vec2F.Zero
  var scale = 4.0f
  var center = Vec2F(-0.01f, -0.01f)

  val startTime = System.currentTimeMillis()

  def release() = {
    assert(!released)
    released = true
  }

  def render(transform: Transform2F, quad: Array[Int], screenSize: Vec2F, mousePos: Vec2F) = {
    assert(!released)

    val view = MatUtil.orthoTransform(0, g.size.x, g.size.y, 0)
    val mvp = (view * transform.scale(screenSize).scale(0.5f).translate(1, 1)).toMat4f

    program.attributeBuffer("aPos", vertexBuffer.buffer, vertexBuffer.numComponents)
    program.uniform("iQuad").foreach(_.putIv(quad))

    program.uniform("iMVP").foreach(_.putMat4F(mvp))
    program.uniform("iResolution").foreach(_.put2F(g.size.toVec2F))
    program.uniform("iTime").foreach(_.putF((System.currentTimeMillis() - startTime) * 0.001f))
    program.uniform("iMouse").foreach(_.put2F(mousePos))
    program.uniform("iIterations").foreach(_.putI(iterations))

    program.draw(ShaderProgram.Mode.Triangles, 0, 6)
  }

}