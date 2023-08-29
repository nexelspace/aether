package space.nexel.aether.jvm.graphics

import space.nexel.aether.core.platform.Event

class InputHandler {

  /** Add key event to queue. Uses internal key repeat logic. */
  def sysKeyPress(pressed: Boolean, code: Int, modifiers: Int) = {
    // add(new Key.KeyEvent(pressed, true, code, modifiers))
    // if (pressed || (repeatEvent != null && repeatEvent.code == code)) {
    //   repeatEvent = if (pressed) new Key.KeyEvent(pressed, false, code, modifiers) else null
    //   repeatTime = System.currentTimeMillis() + REPEAT_INIT
    // }
  }
  def sysCharType(char: Char) = {
    // add(new Key.CharEvent(char))
  }

  def add(event: Event) = {
    // queue.synchronized(queue += event)
  }
}
