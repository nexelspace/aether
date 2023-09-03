package aether.lib.quad.shader

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

import SymSeq._

object SymSeq {

  trait Sym
  class Pointer(var target: Target = null) extends Sym
  class Target() extends Sym

}

class SymSeq(val buffer: ArrayBuffer[Any] = ArrayBuffer[Any]()) {
  var ptr = 0

  export buffer.size

  def add(sym: Any) = {
    buffer += sym
  }
  
  def pointer(): Pointer = {
    val ptr = new Pointer()
    add(ptr)
    ptr
  }

  def target(ptr: Pointer) = {
    assert(ptr.target==null,"Pointer target already assigned")
    val target = new Target()
    ptr.target = target
    add(target)
  }

  def linearize(): Seq[Int] = {
    val linearized = ArrayBuffer[Any]()
    val it = buffer.iterator
    val targets = mutable.Map[Target, Int]()
    while (it.hasNext) {
      it.next() match {
        case i: Int => linearized += i
        case ptr: Pointer => linearized += ptr
        case t: Target => targets.put(t, linearized.size)
      }
    }
    linearized.toSeq.map {
      case i: Int => i
      case p: Pointer => targets(p.target)
    }
  }
  
}
