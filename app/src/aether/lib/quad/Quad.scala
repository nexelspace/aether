package aether.lib.quad

import aether.lib.codec.Codec
import aether.core.buffers.ByteBuffer
import aether.core.types.Vec2I
import aether.core.types.VecExt.*
import aether.core.types.VecExt

object Quad {

  def apply[T](id: T): Leaf[T] = new Leaf[T](id)
  def apply[T](q0: Quad[T], q1: Quad[T], q2: Quad[T], q3: Quad[T]): Quad[T] = new Branch[T](IndexedSeq(q0, q1, q2, q3)).simplifyNode()

  def tabulate[T](init: (Int) ⇒ Quad[T]) = Quad[T](init(0), init(1), init(2), init(3))

  trait Id[T] {
    val id: T
  }

  case class Leaf[T](id: T) extends Quad[T] with Id[T] {

    def maxDepth = 0
    def apply(index: Int): Quad[T] = this

    def updated(index: Int, quad: Quad[T]): Quad[T] = {
      if (quad == this) this
      else new Branch(IndexedSeq.tabulate(4)(i ⇒ if (i == index) quad else this))
    }

  }

  class Branch[T](val quads: IndexedSeq[Quad[T]]) extends Quad[T] {
    def this(x: Quad[T], y: Quad[T], z: Quad[T], w: Quad[T]) = this(IndexedSeq(x, y, z, w))
    def maxDepth = quads.map(_.maxDepth).max + 1

    def apply(index: Int): Quad[T] = quads(index)

    def updated(index: Int, quad: Quad[T]): Quad[T] = {
      if (quads(index) == quad) this
      else new Branch(quads.updated(index, quad))
    }

    def simplifyNode(): Quad[T] = {
      quads.head match {
        case head @ Leaf(id) if (quads.tail.forall {
          case Leaf(cid) => id == cid
          case _         => false
        }) =>
          head
        case _ => this
      }
    }
  }

  class IdBranch[T](quads: IndexedSeq[Quad[T]], val id: T) extends Branch(quads) with Id[T]

  def decorate[T](node: Quad[T], average: IndexedSeq[T] ⇒ T): Quad[T] with Id[T] = {
    def decor(node: Quad[T]): Quad[T] with Id[T] = {
      node match {
        case leaf: Leaf[T]       ⇒ leaf
        case withId: IdBranch[T] ⇒ withId
        case branch: Branch[T] ⇒
          val quads = branch.quads.map(decor)
          new IdBranch(quads, average(quads.map(_.asInstanceOf[Id[T]].id)))
      }
    }
    decor(node)
  }

  def serialize(buffer: ByteBuffer, quad: Quad[Int]): Unit = {
    quad match {
      case b: Branch[Int] ⇒
        buffer.writeVI(0)
        b.quads.foreach(q ⇒ serialize(buffer, q))
      case l: Leaf[Int] ⇒
        //assert(l.id >= 0, "Only positive ids supported")
        buffer.writeVL((l.id & 0xffffffffL) + 1)
    }
  }

  def deserialize(buffer: ByteBuffer): Quad[Int] = {
    buffer.readVL() match {
      case 0 ⇒
        new Branch(deserialize(buffer), deserialize(buffer), deserialize(buffer), deserialize(buffer))
      case n ⇒
        new Leaf((n - 1).toInt)
    }
  }

  trait Visitor[T] {
    def visit(quads: Array[Quad[T]], index: Int): Quad[T]
  }

  val neighbourOffsets = Array(-5, -4, -3, -1, 1, 3, 4, 5)

}

trait Quad[T] /*extends Quad.Id[T]*/ {

  def maxDepth: Int
  def apply(index: Int): Quad[T]

  def updated(index: Int, quad: Quad[T]): Quad[T]

  def apply(depth: Int, pos: Vec2I): Quad[T] = {
    if (depth == 0) this
    else {
      val d = depth - 1
      val p = (pos >> d) & 1
      val index = p.index2
      this(index)(d, pos)
    }
  }

  def updated(depth: Int, pos: Vec2I, quad: Quad[T]): Quad[T] = {
    if (depth == 0) quad
    else {
      val d = depth - 1
      val p = (pos >> d) & 1
      val index = p.index2
      updated(index, this(index).updated(d, pos, quad))
    }
  }

  val indexVertices = (0 until 4 * 4).map(VecExt.V2I.index(4, _)).toArray

  def expand(quads: Array[Quad[T]], center: Int): Array[Quad[T]] = {
    val offset = (VecExt.V2I.index(4, center) << 1) - 1
    indexVertices.map { p =>
      val i: Vec2I = p + offset
      val parent = quads((i >> 1).index(4))
      parent((i & 1).index2)
    }
  }

  //TODO: with prune
  //TODO: with modify
  def visit(depth: Int, visitor: (Array[Quad[T]], Int) => Unit) = {

    def visit(d: Int, quads: Array[Quad[T]], offset: Int): Unit = {
      if (d == 0) {
        visitor(quads, offset)
      } else {
        val children = expand(quads, offset)
        visit(d - 1, children, 5)
        visit(d - 1, children, 6)
        visit(d - 1, children, 9)
        visit(d - 1, children, 10)
      }
    }

    val quads = Array.tabulate(16)(i => this)
    visit(depth, quads, 5)

  }


  def traverse(depth: Int, visitor: (Array[Quad[T]], Int) => Quad[T]): Quad[T] = {

    def traverse(d: Int, quads: Array[Quad[T]], offset: Int): Quad[T] = {
      val node = quads(offset)
      if (node.isInstanceOf[Quad.Leaf[T]] && Quad.neighbourOffsets.forall(o => node == quads(o + offset))) {
        //Log(s"Prune $d, $node")
        // prune
        node
      } else if (d == 0) {
        visitor(quads, offset)
      } else {
        val children = expand(quads, offset)
        Quad(
          traverse(d - 1, children, 5),
          traverse(d - 1, children, 6),
          traverse(d - 1, children, 9),
          traverse(d - 1, children, 10))
      }
    }

    val quads = Array.tabulate(16)(i => this)
    traverse(depth, quads, 5)

  }


}














