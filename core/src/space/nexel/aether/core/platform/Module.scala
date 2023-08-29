package space.nexel.aether.core.platform

import space.nexel.aether.core.platform.Event

object Module {

  abstract class ModuleEvent extends Event

  case class Init() extends ModuleEvent
  /** @time in ms. */
  case class Update(time: Long) extends ModuleEvent
  case object Uninit extends ModuleEvent

}

trait Module {
  def event(event: Event): Unit
}
