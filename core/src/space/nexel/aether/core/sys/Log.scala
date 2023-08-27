package space.nexel.aether.core.sys

object Log {
  private[aether] var global = new Log {
    def apply(message: String) = println(message)    
  }
  inline def apply(message: String) = {
    // TODO: Get source from stack trace
    global(message)
  }
}

trait Log {
  def apply(message: String): Unit
}
