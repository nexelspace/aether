package aether.jvm.graphics

import aether.core.graphics.Display
import aether.core.graphics.Display.*
import aether.core.platform.Platform
import aether.core.platform.Resource
import aether.core.graphics.Graphics
import org.lwjgl.glfw.GLFW._
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWVidMode
import org.lwjgl.opengl.GL
import org.lwjgl.system.MemoryStack.stackPush
import org.lwjgl.system.MemoryUtil.NULL

import java.nio.IntBuffer
import aether.core.platform.NativeResource
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import aether.core.types.Vec2I
import aether.core.platform.Event
import aether.core.platform.Module
import aether.core.platform.Module.*
import aether.core.math.MathD.Log2
import aether.core.platform.Log

object JvmDisplay extends Module {

  def factory(platform: Platform) = new Resource.Factory[Display, Config] {
    given DisplayFactory = this
    def createThis(config: Config) = new JvmDisplay(platform, config)
  }

  // private var graphics: Graphics = null

  // private def init() = {
  //   if (JvmDisplay.graphics == null) {
  //     GLFWErrorCallback.createPrint(System.err).set()
  //     glfwInit()
  //     JvmDisplay.graphics = new JvmGraphics()
  //   }
  // }

  def event(event: Event) = event match {
    case Init(_) =>
      Log("JVMDIsplay.Init")
      // init GLFW
      // will print the error message in System.err.
      GLFWErrorCallback.createPrint(System.err).set()
      // Initialize GLFW. Most GLFW functions will not work before doing this.
      if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW")
    case e: Update =>
    case Resize(disp, size) =>
      disp.asInstanceOf[JvmDisplay].size_ = size
    case Uninit    =>
      // Terminate GLFW and free the error callback
      glfwTerminate()
      glfwSetErrorCallback(null).free()
    case _ =>
  }
}

class JvmDisplay(platform: Platform, val config: Config)(using factory: DisplayFactory)
    extends Display
    /*with NativeResource[Display, Config]*/ {
  val graphics = new JvmGraphics(this)

  private[aether] var size_ = Vec2I(config.size.x, config.size.y)
  def size = size_

  // optional, the current window hints are already the default
  glfwDefaultWindowHints()
    // the window will stay hidden after creation
  glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
  // the window will be resizable
  glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
  // Create the window
  val monitor = if (config.fullscreen) glfwGetPrimaryMonitor() else NULL
  val window = glfwCreateWindow(config.size.x, config.size.y, config.windowTitle, monitor, NULL)
  if (window == NULL)
    throw new RuntimeException("Failed to create the GLFW window")

  new DisplayInput(this, platform.dispatcher)

  // Get the thread stack and push a new frame
  GlUtil.autoCloseTry(stackPush()) { stack =>
    val pWidth: IntBuffer = stack.mallocInt(1)
    val pHeight: IntBuffer = stack.mallocInt(1)
    // Get the window size passed to glfwCreateWindow
    glfwGetWindowSize(window, pWidth, pHeight)
    // Get the resolution of the primary monitor
    val vidmode: GLFWVidMode = glfwGetVideoMode(glfwGetPrimaryMonitor())
    // Center the window
    glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2)
  }
  // Make the OpenGL context current
  glfwMakeContextCurrent(window)
  // Enable v-sync
  glfwSwapInterval(1)
  // Make the window visible
  glfwShowWindow(window)

  GL.createCapabilities()

  /** Pointer grabbed / locked. */
  var pointerGrab = false

  def render(callback: Display => Unit): Unit = {
    glfwPollEvents()
    if (glfwWindowShouldClose(window)) {
      platform.exit()
    }
    graphics.render(this, callback)
    glfwSwapBuffers(window)

  }

  def release() = {
    factory.released(this)
    glfwFreeCallbacks(window)
    glfwDestroyWindow(window)
  }

  // TODO
  // def display() = {
  //   // Set the clear color
  //   glClearColor(1.0f, 0.0f, 0.0f, 0.0f)
  //   NRenderer.renderingThread = true
  //   if (Sys.instance != null) Sys.instance.asInstanceOf[NSys].display(this)
  //   NRenderer.renderingThread = false
  //   glfwSwapBuffers(window)
  // }

  def grabPointer(grab: Boolean) = {
    pointerGrab = grab
    // Log(s"Display.grabPointer($grab)")
    glfwSetInputMode(window, GLFW_CURSOR, if (grab) GLFW_CURSOR_DISABLED else GLFW_CURSOR_NORMAL)
  }

  def clear(r: Float, g: Float, b: Float, a: Float): Unit = {
    import org.lwjgl.opengl.GL11._
    glClearColor(r, g, b, a)
    glClear(GL_COLOR_BUFFER_BIT)
  }

}
