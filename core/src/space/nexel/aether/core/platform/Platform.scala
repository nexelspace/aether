package space.nexel.aether.core.platform
import space.nexel.aether.core.graphics.Display
import space.nexel.aether.core.base.*

object Platform {
  case class Update(time: Long) extends Event
}

trait Platform(modules: Seq[Module]) {

  val displayFactory: Resource.Factory[Display, Display.Config]

  val dispatcher: Dispatcher = new Dispatcher()
  given Dispatcher = dispatcher

  def log: Log
  def base: Base

  def resource(source: Any): Base = {
    //TODO
    val path = source.getClass().getName().split("\\.").dropRight(1).mkString("/")
    val nb = s"app/src/$path"
    Log(s"Resource base $nb")
    base.base(nb)
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
