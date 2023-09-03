package aether.app.canvas

import aether.lib.quad.QuadGrid
import aether.lib.quad.Quad
import aether.lib.quad.shader.QuadShader
import aether.core.types.Vec2I
import aether.core.util.Bits
import aether.core.graphics.Texture
import aether.core.platform.Log
import aether.core.graphics.Graphics

object CanvasQuad {
  val testQuad = Quad(
    Quad(Quad(0xff8888ff), Quad(0x88ff88ff), Quad(0x8888ffff), Quad(0x88888888)),
    Quad(0xff0000ff),
    Quad(0x00ff00ff),
    Quad(Quad(0xff0088ff), Quad(0x880088ff), Quad(0x8800ffff), Quad(0x88008888))
  )
  def apply()(using Graphics): CanvasQuad = new CanvasQuad(Quad(0))
  def init()(using Graphics): CanvasQuad = new CanvasQuad(testQuad)
}

class CanvasQuad(init: Quad[Int])(using Graphics) {
  def initCanvas = new QuadGrid(0, init, init.maxDepth)

  var grid: QuadGrid[Int] = initCanvas

  var bufferTex: Texture = _
  createBufferTexture(1024)
  var canvasModified = true
  serializeQuad()

  def textureSize(minSize: Int): Vec2I = {
    val minEdge = (Math.sqrt(minSize - 1) + 1).toInt
    val edgeShift = Bits.indexOfMSB(minEdge - 1) + 1
    Vec2I(1 << edgeShift, 1 << edgeShift)
  }

  def createBufferTexture(minSize: Int) = {
    if (bufferTex != null) bufferTex.release()
    val bufFormat = Texture.Format.RGBA_8888_UI
    val size = textureSize(minSize)
    Log(s"Create buffer texture $minSize -> $size")
    bufferTex = Texture(Texture.Flag.Writable, size.x, size.y, bufFormat)
  }

  def serializeQuad() = {
    val quadArray: Seq[Int] = QuadShader.linearize(grid.quad)
    val texSize = bufferTex.size.x * bufferTex.size.y
    if (quadArray.size > texSize) {
      Log(s"Resize texture ${quadArray.size}, $texSize")
      createBufferTexture(quadArray.size)
    }
    val b = bufferTex.buffer.getOrElse {
      sys.error("Texture .buffer is not available")
    }
    b.clear()

    for (c <- quadArray) {
      b.writeI(c)
    }
    bufferTex.bufferModified()
    canvasModified = false
  }

  def setGrid(root: QuadGrid[Int]) = {
    grid = root
    // serializeQuad()
    canvasModified = true
  }

  def update(pos: Vec2I, color: Int) = {
    grid.update(pos, color)
    // serializeQuad()
    canvasModified = true
  }

  def clear() = {
    grid = new QuadGrid(0, Quad(0), Quad(0).maxDepth)
    // serializeQuad()
    canvasModified = true
  }

  def isEmpty = grid.quad == Quad(grid.emptyCell)

}
