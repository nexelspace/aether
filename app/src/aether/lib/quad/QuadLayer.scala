package aether.lib.quad

import aether.lib.codec.Codec
import aether.core.buffers.ByteBuffer

object QuadLayer extends Codec[QuadLayer](1) {
  def decode(buffer: ByteBuffer): QuadLayer = {
    val serial = buffer.readVI()
    val grid = QuadGrid.decode(buffer)
    new QuadLayer(serial, grid)
  }
  def encode(buffer: ByteBuffer, obj: QuadLayer): Unit = {
    buffer.writeVI(obj.serial)
    QuadGrid.encode(buffer, obj.root)
  }
}

class QuadLayer(val serial: Int, val root: QuadGrid[Int]) {

  /** Merge given layer to this layer. */
  def merge(layer: QuadLayer): Unit = {
    root.merge(layer.root)
  }

  def copy: QuadLayer = new QuadLayer(serial, root.copy)

}
