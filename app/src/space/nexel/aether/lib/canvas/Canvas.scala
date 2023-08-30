package space.nexel.aether.lib.canvas

import space.nexel.aether.core.types.Vec2F
import space.nexel.aether.core.types.Vec2I

import Canvas.*
import space.nexel.aether.core.types.RectF
import space.nexel.aether.lib.types.Tx2FAxis
import space.nexel.aether.lib.font.Font
import space.nexel.aether.core.graphics.Texture

object Canvas {
  // case class Render(canvas: Canvas, view: RectF) extends Event

  // trait Paint {
  //   def paint(canvas: Canvas, view: RectF): Unit
  // }
}

trait Canvas {

  /** Size of Canvas viewport. */
  // def size: Vec2F = view.size

  def tx: Tx2FAxis
  /** Vieport & render area relative to current transform. */
  def view: RectF

  /** Create Canvas viewport.
   * @param area Location of viewport in current Canvas space.
   * @param size Viewport size of new canvas.
   * @param tx Tranformation 
   */
  def viewport(area: RectF, tx: Tx2FAxis = Tx2FAxis.Identity)(enclosed: Canvas => Unit): Unit = {
    val intersect = view.intersect(area)
    val newArea = tx.inv.transformArea(RectF(Vec2F.Zero, intersect.size))
    val newTx = this.tx.translated(intersect.begin) * tx
    // Log(s"Canvas viewport $area->$intersect->$newArea, $tx->$newTx")
    assert(view.isInside(intersect), s"Viewport area $intersect exceeds previous area $view")
    flush()
    val canvas = copy(newArea, newTx)
    enclosed(canvas)
    canvas.flush()
  }
  def copy(area: RectF, tx: Tx2FAxis): Canvas
  
  def flush(): Unit

  def clear(argb: Int): Unit

  // final def drawString(pos: Vec2F, font: Font, string: String): Unit = {
  //   drawString(RectF(pos.x, pos.y, font.width(string), font.height), font, string)
  // }
  def fillRect(area: RectF, color: Int): Unit

  def drawTexture(area: RectF, texture: Texture): Unit

  def drawString(pos: Vec2F, font: Font, string: String): Unit

  override def toString() = s"Canvas(tx $tx, view $view)"

}
