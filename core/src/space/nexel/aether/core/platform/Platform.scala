package space.nexel.aether.core.platform
import space.nexel.aether.core.graphics.Display

object Platform {
  case class Update(time: Long) extends Event
}

trait Platform {
  def log: Log

  def modules: Seq[Module]

  // Factories
  val displayFactory: Resource.Factory[Display, Display.Config]

  val dispatcher: Dispatcher = new Dispatcher()


  def runApp(app: Module) = {
    new RenderLoop(this, modules :+ app).run()
  }

}
