package space.nexel.aether.core.sys

object Platform {
  case class Update(time: Long) extends Event
}

trait Platform {
  def log: Log

  // Factories
  val displayFactory: Resource.Factory[Display, Display.Config]

  val events: EventQueue

}
