package space.nexel.aether.core.platform

import scala.collection.mutable
import space.nexel.aether.core.platform.Dispatcher.CallbackEvent

object Dispatcher {
  class CallbackEvent(callback: => Unit) extends Event
}

class Dispatcher {

  val queue = new mutable.Queue[Event]()

  def add(event: Event): Unit = {
    synchronized {
      queue.enqueue(event)
    }
  }

  def getEvent: Option[Event] = {
    synchronized {
      queue.headOption
    }
  }
  def dispatch(handler: => Unit): Unit = {
    add(CallbackEvent(handler))
  }
}
