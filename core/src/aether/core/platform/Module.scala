package aether.core.platform

import aether.core.platform.Event

object Module {

  abstract class ModuleEvent extends Event

  case class Init(platform: Platform) extends ModuleEvent
  /** @time in ms. */
  case class Update(time: Long) extends ModuleEvent
  case object Uninit extends ModuleEvent

}

trait Module {
  def event(event: Event): Unit
}
