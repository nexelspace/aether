package aether.lib.quad.graph

import DisplayGraph._
import aether.core.types.Vec2I
import aether.core.types.RectI
import aether.lib.quad.Quad
import aether.core.types.RectF

object DisplayGraph {


}

class DisplayGraph(var quad: Quad[Seq[View]] = null, var unitDepth: Int = 0) {

  val emptyQuad = Quad(Seq[View]())

  def expand() = {
    quad =
      Quad.tabulate { i ⇒
        Quad.tabulate { j ⇒
          if (i == (j ^ 3)) quad(i) else emptyQuad
        }
      }
    unitDepth = 1 + unitDepth
  }

  // def expand(pos: Vec2I): Unit = {
  //   while (!bounds.isInside(pos)) expand()
  // }
}
