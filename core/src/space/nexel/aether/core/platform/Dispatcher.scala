package space.nexel.aether.core.platform

import scala.collection.mutable
import space.nexel.aether.core.platform.Dispatcher.CallbackEvent
import space.nexel.aether.core.input.KeyEvent
import scala.collection.mutable.ListBuffer

object Dispatcher {
  case class CallbackEvent(callback: () => Unit) extends Event
}

class Dispatcher {

  val queue = new mutable.Queue[Event]()

  def add(event: Event): Unit = {
    synchronized {
      queue.enqueue(event)
    }
  }

  def getEvent(): Option[Event] = {
    synchronized {
      if (queue.size==0) None
      else Some(queue.dequeue())
    }
  }
  
  def dispatch(handler: => Unit): Unit = {
    add(CallbackEvent(() => handler))
  }

  // common

  val REPEAT_INIT = 250
  val REPEAT_TIME = 50

  var repeatTime = 0L
  var repeatEvent: KeyEvent = _

  /** Add key event to queue. Uses internal key repeat logic. */
  def sysKeyPress(pressed: Boolean, code: Int, modifiers: Int) = {
    add(KeyEvent(pressed, true, code, modifiers))
    if (pressed || (repeatEvent != null && repeatEvent.code == code)) {
      repeatEvent = if (pressed) KeyEvent(pressed, false, code, modifiers) else null
      repeatTime = System.currentTimeMillis() + REPEAT_INIT
    }
  }
}
