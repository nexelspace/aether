package space.nexel.aether.core.input

import scala.collection.mutable
import space.nexel.aether.core.platform.Log

object KeyMap {
  case class Item(target: Int, source: Int, count: Int = 1)

  /** For platform-specific keymapping. Keycodes:
    * https://docs.google.com/spreadsheets/d/1C9sLOyFhTDPvVXbKd-4QpDvNNxpfOWg0QJZaV82w9JE/
    */
  def create(mapping: Seq[Item]): Map[Int, Int] = {
    for {
      keymap <- mapping
      index <- 0 until keymap.count
      entry = (keymap.source + index -> (keymap.target + index))
    } yield entry
  }.toMap

  def apply(mapping: Item*): KeyMap = {
    new KeyMap(create(mapping))
  }
}

class KeyMap(val map: Map[Int, Int]) {
  Log(s"Keymap mapping $map")
  val undefinedKeys = mutable.SortedSet[Int]()

  def apply(code: Int, defaultKey: Int): Int = {
    map.get(code).getOrElse {
      if (!undefinedKeys.contains(code)) {
        Log(s"Undefined key code $code")
        undefinedKeys += code
      }
      defaultKey
    }
  }

  def reportUndefinedKeys() = {
    if (undefinedKeys.size > 0) {
      Log(s"Undefined keys: $undefinedKeys")
    }
  }

}
