package space.nexel.aether.jvm.graphics

import org.lwjgl.glfw.GLFW._
import space.nexel.aether.core.input.PointerEvent.*
import space.nexel.aether.core.platform.Display
import space.nexel.aether.core.platform.Display.*
import space.nexel.aether.core.types.Vec2F
import space.nexel.aether.core.types.Vec2I

class DisplayInput(display: JvmDisplay, input: InputHandler) {

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
        case GLFW_PRESS =>
          input.sysKeyPress(true, akey, mods)
        case GLFW_RELEASE =>
          input.sysKeyPress(false, akey, mods)
        case GLFW_REPEAT =>
      }
  )

  def charCallback(window: Long, codepoint: Int) = {
    // Log(s"Char callback $window, $codepoint")
    input.sysCharType(codepoint.toChar)
  }

  def mouseCallback(window: Long, button: Int, action: Int, mods: Int) = {
    // Log(s"Mouse callback $window, $button, $action, $mods at $mousePos")
    mousePressed = action match {
      case GLFW_PRESS   => true
      case GLFW_RELEASE => false
    }
    import MouseButton.*
    val sysButton = IndexedSeq(Left, Right, Middle)
    input.add(MouseButton(mousePressed, mousePos, sysButton(button)))
  }

  def mousePosCallback(window: Long, x: Double, y: Double) = {
    val oldPos = mousePos
    mousePos = Vec2F(x.toFloat, y.toFloat)
    if (display.pointerGrab) {
      input.add(MouseRelative(mousePos - oldPos, 0))
    } else {
      input.add(MouseMove(mousePressed, mousePos, 0))
    }
  }

  def mouseWheelCallback(window: Long, xOffset: Double, yOffset: Double) = {
    val event = new MouseWheel(mousePos, yOffset.toFloat)
    input.add(event)
  }

  def windowSizeCallback(window: Long, width: Int, height: Int) = {
    display.size_ = Vec2I(width, height)
    input.add(Resize(display, display.size))
  }

  def windowFocusCallback(window: Long, focused: Boolean) = {
    input.add(Focus(display, focused))
  }
}
