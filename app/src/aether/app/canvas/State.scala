package aether.app.canvas

import aether.lib.quad.QuadGrid
import aether.lib.quad.QuadModel
import aether.core.graphics.Graphics

class State(using Graphics) {

  var color = 0xffffffff
  // var grid: QuadGrid[Int] = _
  // val data = new SyncData(new CanvasQuad())

  val baseCanvas = CanvasQuad.init()
  val changeCanvas = CanvasQuad()
  val paintCanvas = CanvasQuad()

  // val model = new QuadModel(CanvasQuad.testQuad)

}
