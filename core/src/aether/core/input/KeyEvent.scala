package aether.core.input

import aether.core.platform.Event

object KeyEvent {

  /** Event keyCode constants. Using GLFW keys: https://www.glfw.org/docs/3.3/group__keys.html
    */
  object Code extends IntEnum {
    val UNKNOWN = -1
    val SPACE = 32
    val APOSTROPHE = 39
    val COMMA = 44
    val MINUS = 45
    val PERIOD = 46
    val SLASH = 47
    val SEMICOLON = 59
    val EQUAL = 61
    val LEFT_BRACKET = 91
    val BACKSLASH = 92
    val RIGHT_BRACKET = 93
    val GRAVE_ACCENT = 96
    val WORLD_1 = 161
    val WORLD_2 = 162
    val ESCAPE = 256
    val ENTER = 257
    val TAB = 258
    val BACKSPACE = 259
    val INSERT = 260
    val DELETE = 261
    val RIGHT = 262
    val LEFT = 263
    val DOWN = 264
    val UP = 265
    val PAGE_UP = 266
    val PAGE_DOWN = 267
    val HOME = 268
    val END = 269
    val CAPS_LOCK = 280
    val SCROLL_LOCK = 281
    val NUM_LOCK = 282
    val PRINT_SCREEN = 283
    val PAUSE = 284
    val KP_DECIMAL = 330
    val KP_DIVIDE = 331
    val KP_MULTIPLY = 332
    val KP_SUBTRACT = 333
    val KP_ADD = 334
    val KP_ENTER = 335
    val KP_EQUAL = 336
    val LEFT_SHIFT = 340
    val LEFT_CONTROL = 341
    val LEFT_ALT = 342
    val LEFT_SUPER = 343
    val RIGHT_SHIFT = 344
    val RIGHT_CONTROL = 345
    val RIGHT_ALT = 346
    val RIGHT_SUPER = 347
    val MENU = 348
    val N0, N1, N2, N3, N4, N5, N6, N7, N8, N9 = enumSeries(0x30)
    val A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z = enumSeries(65)
    val F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12 = enumSeries(290)
    val NUM0, NUM1, NUM2, NUM3, NUM4, NUM5, NUM6, NUM7, NUM8, NUM9 = enumSeries(320)
  }

  object Modifier extends IntEnum {
    val Shift, Control, Meta, Alt, AltGraph = enumBits(0)
  }

  // -- Aliases
  object KeyPressed {
    def unapply(e: KeyEvent): Option[Int] = if (e.active && e.changed) Some(e.code) else None
  }

  case class CharEvent(char: Char) extends Event
}

/** @active
  *   Is key pressed.
  * @changed
  *   False for repeated key.
  */
case class KeyEvent(val active: Boolean, val changed: Boolean, code: Int, mods: Int) extends Event {
  //    def control: Boolean = (mods & Event.KeyModifier.Control) != 0
  //    def shift: Boolean = (mods & Event.KeyModifier.Shift) != 0
  //    def alt: Boolean = (mods & Event.KeyModifier.Alt) != 0
}
