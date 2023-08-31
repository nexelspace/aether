package space.nexel.aether.jvm.graphics

import org.lwjgl.glfw.GLFW._
import space.nexel.aether.core.input.PointerEvent.*
import space.nexel.aether.core.graphics.Display
import space.nexel.aether.core.graphics.Display.*
import space.nexel.aether.core.types.Vec2F
import space.nexel.aether.core.types.Vec2I
import space.nexel.aether.core.platform.Dispatcher
import space.nexel.aether.core.input.KeyEvent
import space.nexel.aether.core.input.KeyEvent.CharEvent

class DisplayInput(display: JvmDisplay, dispatcher: Dispatcher) {

  var mousePressed = false
  var mousePos: Vec2F = Vec2F(0)

  val window = display.window
  // glfwSetKeyCallback(window, keyCallback)
  glfwSetCharCallback(window, charCallback)
  glfwSetMouseButtonCallback(window, mouseCallback)
  glfwSetCursorPosCallback(window, mousePosCallback)
  glfwSetScrollCallback(window, mouseWheelCallback)
  glfwSetWindowSizeCallback(window, windowSizeCallback)
  glfwSetWindowFocusCallback(window, windowFocusCallback)

  glfwSetKeyCallback(
    window,
    (window: Long, key: Int, scancode: Int, action: Int, mods: Int) =>
      // Log(s"Key callback $window, $key, $scancode, $action,  $mods")
      val akey = key // aether uses GLFW keycode constants
      action match {
        case GLFW_PRESS   => dispatcher.sysKeyPress(true, akey, mods)
        case GLFW_RELEASE => dispatcher.sysKeyPress(false, akey, mods)
        case GLFW_REPEAT  =>
      }
  )

  def sysCharType(char: Char) = {
    dispatcher.add(new CharEvent(char))
  }

  def charCallback(window: Long, codepoint: Int) = {
    // Log(s"Char callback $window, $codepoint")
    sysCharType(codepoint.toChar)
  }

  def mouseCallback(window: Long, button: Int, action: Int, mods: Int) = {
    // Log(s"Mouse callback $window, $button, $action, $mods at $mousePos")
    mousePressed = action match {
      case GLFW_PRESS   => true
      case GLFW_RELEASE => false
    }
    import MouseButton.*
    val sysButton = IndexedSeq(Left, Right, Middle)
    dispatcher.add(MouseButton(mousePressed, mousePos, sysButton(button)))
  }

  def mousePosCallback(window: Long, x: Double, y: Double) = {
    val oldPos = mousePos
    mousePos = Vec2F(x.toFloat, y.toFloat)
    if (display.pointerGrab) {
      dispatcher.add(MouseRelative(mousePos - oldPos, 0))
    } else {
      dispatcher.add(MouseMove(mousePressed, mousePos, 0))
    }
  }

  def mouseWheelCallback(window: Long, xOffset: Double, yOffset: Double) = {
    val event = new MouseWheel(mousePos, yOffset.toFloat)
    dispatcher.add(event)
  }

  def windowSizeCallback(window: Long, width: Int, height: Int) = {
    display.size_ = Vec2I(width, height)
    dispatcher.add(Resize(display, display.size))
  }

  def windowFocusCallback(window: Long, focused: Boolean) = {
    dispatcher.add(Focus(display, focused))
  }
}
