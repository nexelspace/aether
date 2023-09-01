package aether.js.graphics

// import aether.core.input.Input
import org.scalajs.dom
import aether.core.input.KeyEvent
import aether.core.input.PointerEvent.*
import aether.core.types.Vec2F
import org.scalajs.dom.HTMLCanvasElement
import scala.scalajs.js.Dynamic
import aether.core.platform.*
import aether.core.input.TouchEvent
import aether.core.input.TouchEvent.Type
import aether.core.input.KeyMap

object JsEvents {

  var pointerLock = false
  def locked = dom.document.asInstanceOf[Dynamic].pointerLockElement != null

  def initMouseEvents(canvas: HTMLCanvasElement)(using dispatcher: Dispatcher) = {
    val offsetX = canvas.getBoundingClientRect().left.toFloat
    val offsetY = canvas.getBoundingClientRect().top.toFloat

    def button(e: dom.MouseEvent) = e.button match {
      case 0 => MouseButton.Left
      case 1 => MouseButton.Middle
      case 2 => MouseButton.Right
    }

    def pos(e: dom.MouseEvent) = Vec2F(e.clientX.toFloat - offsetX, e.clientY.toFloat - offsetY)

    def createMouseEvent(press: Boolean, e: dom.MouseEvent) = {
      dispatcher.add(MouseButton(press, pos(e), button(e)))
    }

    canvas.onmousedown = (e: dom.MouseEvent) => {
      createMouseEvent(true, e)
      checkPointerLock(canvas)
      //      e.preventDefault()
    }
    canvas.onmouseup = (e: dom.MouseEvent) => {
      createMouseEvent(false, e)
      //      e.preventDefault()
    }
    canvas.onmousemove = (e: dom.MouseEvent) => {
      if (locked) {
        val x = e.asInstanceOf[Dynamic].movementX.asInstanceOf[Float]
        val y = e.asInstanceOf[Dynamic].movementY.asInstanceOf[Float]
        dispatcher.add(MouseRelative(Vec2F(x, y), button(e)))

      } else {
        val buttons = e.asInstanceOf[Dynamic].which.asInstanceOf[Int]
        dispatcher.add(MouseMove(buttons != 0, pos(e), button(e)))
      }
      //      e.preventDefault()
    }
    canvas.onmousewheel = (e: dom.MouseEvent) => {
      val delta = e.asInstanceOf[Dynamic].wheelDelta.asInstanceOf[Int] / 120
      //      Log("Delta: "+delta)
      //      createMouseEvent(Event.Type.PointerWheel, e.detail)
      dispatcher.add(MouseWheel(pos(e), delta))
      e.preventDefault() // prevent zooming
    }

    canvas.oncontextmenu = (e: dom.MouseEvent) => {
      e.preventDefault() // prevent default context menu pop-up
    }

  }

  def handeTouch(e: dom.TouchEvent, eventType: TouchEvent.Type)(using dispatcher: Dispatcher): Unit = {
    e.preventDefault()
    val target = IndexedSeq.tabulate(e.targetTouches.length)(e.targetTouches.item)
    val allTouches: Map[Int, Vec2F] =
      if (target.isEmpty) {
        Map()
      } else {
        val max = target.map(_.identifier.toInt).max
        val result = target.map {
          touch => (touch.identifier.toInt -> Vec2F(touch.clientX.toFloat, touch.clientY.toFloat))
        }
        result.toMap
      }
    val changed = IndexedSeq.tabulate(e.changedTouches.length)(e.changedTouches.item)
    for (c <- changed) {
      val event = new TouchEvent(eventType, c.identifier.toInt, Vec2F(c.clientX.toFloat, c.clientY.toFloat), allTouches)
      dispatcher.add(event)
    }
  }

  def initTouchEvents(canvas: HTMLCanvasElement)(using dispatcher: Dispatcher) = {
    canvas.addEventListener("touchstart", handeTouch(_, TouchEvent.Type.Start), false)
    canvas.addEventListener("touchend", handeTouch(_, TouchEvent.Type.End), false)
    // canvas.addEventListener("touchcancel", touch("touchcancel", _), false)
    canvas.addEventListener("touchmove", handeTouch(_, TouchEvent.Type.Move), false)
  }

  private def checkPointerLock(canvas: HTMLCanvasElement) = {
    if (pointerLock ^ locked) {
      //      pointerLocked = pointerLock
      if (pointerLock) {
        //          val canvas = dom.document.getElementById("elemental").asInstanceOf[HTMLCanvasElement]
        //val canvas = dom.document.getElementById("elemental").asInstanceOf[Dynamic]
        val result = canvas.asInstanceOf[Dynamic].requestPointerLock()
      } else {
        dom.document.asInstanceOf[Dynamic].exitPointerLock()
      }
    }
  }

  def initKeyEvents()(using dispatcher: Dispatcher) = {
    dom.document.onkeydown = (e: dom.KeyboardEvent) => createKeyEvent(true, e)
    dom.document.onkeyup = (e: dom.KeyboardEvent) => createKeyEvent(false, e)
    // e.preventDefault() // prevents default F1 help
    // e.stopPropagation()

    def createKeyEvent(press: Boolean, e: dom.KeyboardEvent) = {
      if (!e.repeat) {
        val code = keyMap(e.keyCode, e.keyCode)
        val char = e.charCode match {
          case 0 if (e.key.length == 1) => e.key.head
          case 0                        => 0.toChar
          case code                     => code.toChar
        }
        val mods = (if (e.ctrlKey) KeyEvent.Modifier.Control else 0) //TODO: add other modifiers
        dispatcher.sysKeyPress(press, code, mods)
        if (char > 0) dispatcher.add(KeyEvent.CharEvent(char))
      }
    }
  }

  import KeyEvent.Code._
  import KeyMap.Item

  val keyMap = KeyMap(
    Item(N0, 48, 10),
    Item(A, 65, 26),
    Item(NUM0, 96, 10),
    Item(F1, 112, 12),
    Item(BACKSPACE, 8),
    Item(TAB, 9),
    Item(ENTER, 13),
    Item(LEFT_SHIFT, 16),
    Item(RIGHT_SHIFT, 16),
    Item(LEFT_CONTROL, 17),
    Item(RIGHT_CONTROL, 17),
    Item(LEFT_ALT, 18),
    Item(RIGHT_ALT, 18),
    Item(PAUSE, 19),
    Item(CAPS_LOCK, 20),
    Item(ESCAPE, 27),
    Item(SPACE, 32),
    Item(PAGE_UP, 33),
    Item(PAGE_DOWN, 34),
    Item(END, 35),
    Item(HOME, 36),
    Item(LEFT, 37),
    Item(UP, 38),
    Item(RIGHT, 39),
    Item(DOWN, 40),
    Item(PRINT_SCREEN, 44),
    Item(INSERT, 45),
    Item(DELETE, 46),
    Item(LEFT_SUPER, 91),
    Item(RIGHT_SUPER, 92),
    Item(MENU, 93),
    Item(KP_MULTIPLY, 106),
    Item(KP_ADD, 107),
    Item(KP_SUBTRACT, 109),
    Item(KP_DECIMAL, 110),
    Item(KP_DIVIDE, 111),
    Item(NUM_LOCK, 144),
    Item(SCROLL_LOCK, 145),
    Item(SEMICOLON, 186),
    Item(EQUAL, 187),
    Item(COMMA, 188),
    Item(MINUS, 189),
    Item(PERIOD, 190),
    Item(SLASH, 191),
    Item(GRAVE_ACCENT, 192),
    Item(LEFT_BRACKET, 219),
    Item(BACKSLASH, 220),
    Item(RIGHT_BRACKET, 221),
    Item(APOSTROPHE, 222)
  )
}
