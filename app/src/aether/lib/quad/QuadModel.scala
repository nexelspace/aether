package aether.lib.quad

import scala.collection.mutable.Buffer

object QuadModel {

  // def apply(): QuadModel = new QuadModel()

}

class QuadModel(rootQuad: Quad[Int]) {
  
  val emptyCell = 0
  var serial = 0

  val layers = Buffer[QuadLayer]()
  var fullMerge: Option[QuadLayer] = None

  def lastLayer = layers.last

  def mergeLast(): Unit = ???
  def mergeHead(): Unit = {
    val next = layers.remove(1)
    layers.head.merge(next)
  }

  def create: QuadLayer = {
    fullMerge = None
    serial += 1
    val layer = new QuadLayer(serial, QuadGrid(emptyCell))
    layers += layer
    layer
  }

  def fullModel: QuadLayer = {
    if (fullMerge.isEmpty) {
      val full = layers.head.copy
      layers.tail.foreach(full.merge)
      fullMerge = Some(full)
    }
    fullMerge.get
  }
}
