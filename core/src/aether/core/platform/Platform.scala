package aether.core.platform
import aether.core.graphics.Display
import aether.core.base.*
import Dispatcher.CallbackEvent

object Platform {
  case class Update(time: Long) extends Event
}

trait Platform(modules: Seq[Module]) {

  val displayFactory: Resource.Factory[Display, Display.Config]

  val dispatcher: Dispatcher = new Dispatcher()
  given Dispatcher = dispatcher

  val log: Log
  val base: Base
  val resourceBase: Base

  def resource(source: Any): Base = {
    val path = source.getClass().getName().split("\\.").dropRight(1).mkString("/")
    resourceBase.base(path)
  }

  // Initialize system modules before instantiating App
  init()

  def init() = {
    Log("Module init")
    modules.foreach(_.event(Module.Init(this)))
  }

  // called by renderloop
  def uninit() = {
    Log("Module uninit")
    modules.reverse.foreach(_.event(Module.Uninit))
  }

  private var running = true

  def exit(): Unit = running = false

  def runApp(app: Module) = {
    app.event(Module.Init(this))
    val mods = modules :+ app

    run {
      var processEvents = true
      while (processEvents) {
        dispatcher.getEvent() match {
          case Some(CallbackEvent(callback)) => callback()
          case Some(event) => mods.foreach(_.event(event))
          case None        => processEvents = false
        }
      }

      displayFactory.instances.foreach(_.render { disp =>
        assert(disp.graphics.target != null)
        mods.foreach(_.event(Display.Paint(disp)))
      })
      if (!running) {
        app.event(Module.Uninit)
        uninit()
      }
      running
    }
    // JVM runs in this thread and returns when app exits
    // JS starts a render loop and returns immediately
  }

  def run(loop: => Boolean): Unit

}
