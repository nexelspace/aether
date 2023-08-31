package space.nexel.aether.core.platform

object Log {
  private[aether] var global = new Log {
    def apply(message: String) = println(message)    
  }
  inline def apply(message: String) = {
    // TODO: Get source from stack trace
    global(message)
  }

  // Stack trace

  def toString(t: Throwable): String = t.getMessage+" "+stackTrace(t).take(4).map(_.simpleRef).mkString(" < ")

  case class StackElement(className: String, method: String, file: String, line: Int) {
    def packageName = className.substring(0, className.lastIndexOf("."))
    def simpleClass = className.substring(className.lastIndexOf(".")+1)
    val cleanName = simpleClass.replaceAll("[\\<>]", "").replaceAll("\\$$", "")
    def simpleRef = s"$cleanName:$line"
  }
  
  def stackTrace(context: Throwable): List[StackElement] = {
    context.getStackTrace
      .map(t => StackElement(t.getClassName, t.getMethodName, t.getFileName, t.getLineNumber))
      .toList
  }

  def stackSource(context: Throwable, elementCount: Int = 3): String = {
     stackTrace(context).drop(1).take(elementCount).map(_.simpleRef).mkString(" < ")
  }
}

trait Log {
  def apply(message: String): Unit
}
