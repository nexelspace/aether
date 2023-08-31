package space.nexel.aether.core.platform
import space.nexel.aether.core.graphics.Display

object Platform {
  case class Update(time: Long) extends Event
}

trait Platform(modules: Seq[Module]) {
  def log: Log

  // def modules: Seq[Module]

  // Factories
  val displayFactory: Resource.Factory[Display, Display.Config]

  val dispatcher: Dispatcher = new Dispatcher()

  var loop: RenderLoop = null

  // protected def init() = {
    Log("Module init")
    modules.foreach(_.event(Module.Init(this)))
  // }
    
  def exit(): Unit = loop.stop()

  def runApp(app: Module) = {
    app.event(Module.Init(this))
    loop = new RenderLoop(this, modules :+ app)
    loop.run()
  }

}
