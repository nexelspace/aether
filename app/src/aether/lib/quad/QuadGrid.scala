package aether.lib.quad

import aether.core.buffers.ByteBuffer

import aether.lib.codec.Codec
import aether.core.types.RectI
import aether.core.types.Vec2I
import aether.lib.quad.Quad.Leaf
import aether.lib.quad.Quad.Branch

object QuadGrid extends Codec[QuadGrid[Int]](1) {
  def apply(defaultCell: Int): QuadGrid[Int] = {
    new QuadGrid(defaultCell)
  }

  // val codecVersion = 1
  def decode(buffer: ByteBuffer): QuadGrid[Int] = {
    // assert(version == 0 || version == codecVersion, s"Unsupported codec version $version, current $codecVersion")
    val defaultCell = buffer.readVI()
    val depth = buffer.readVI()
    val quad = Quad.deserialize(buffer)
    new QuadGrid(defaultCell, quad, depth)
  }

  def encode(buffer: ByteBuffer, grid: QuadGrid[Int]): Unit = {
    // assert(version == 0 || version == codecVersion, s"Unsupported codec version $version, current $codecVersion")
    buffer.writeVI(grid.emptyCell)
    buffer.writeVI(grid.depth)
    Quad.serialize(buffer, grid.quad)
    // codecVersion
  }

}

class QuadGrid[T](val emptyCell: T, var quad: Quad[T] = null, var depth: Int = 1) extends MutableGrid[T] {

  val emptyQuad = Quad(emptyCell)
  if (quad == null) quad = emptyQuad
  def size = 1 << depth
  def radius = size / 2
  def offset = -radius

  def copy = new QuadGrid(emptyCell, quad, depth)

  def bounds: RectI = RectI(offset, offset, size, size)

  def apply(pos: Vec2I): T = {
    val q =
      if (!bounds.isInside(pos)) {
        emptyQuad
      } else {
        quad(depth, pos + offset)
      }
    q match {
      case leaf: Quad.Leaf[T] => leaf.id
      case _                  => sys.error(s"Not leaf: $q")
    }
  }

  def expand() = {
    quad =
      Quad.tabulate { i ⇒
        Quad.tabulate { j ⇒
          if (i == (j ^ 3)) quad(i) else emptyQuad
        }
      }
    depth = 1 + depth
  }

  def expand(pos: Vec2I): Unit = {
    while (!bounds.isInside(pos)) expand()
  }

  def update(pos: Vec2I, id: T): Unit = {
    expand(pos)
    quad = quad.updated(depth, pos + offset, Quad(id))
  }

  def brush(pos: Vec2I, brush: Quad[T]) = {
    ???
  }

  // TODO use undeficed cell instead of defaultCell
  def merge(withGrid: QuadGrid[T]/*, undefined: T*/): Unit = {
    while (depth < withGrid.depth) expand()
    while (depth > withGrid.depth) withGrid.expand()
    quad = merge(quad, withGrid.quad)

    def merge(a: Quad[T], b: Quad[T]): Quad[T] = {
      b match {
        case b @ Leaf(d)  => if (d == emptyCell) a else b
        case b: Branch[T] => Quad(merge(a(0), b(0)), merge(a(1), b(1)), merge(a(2), b(2)), merge(a(3), b(3)))
      }
    }
  }

}
